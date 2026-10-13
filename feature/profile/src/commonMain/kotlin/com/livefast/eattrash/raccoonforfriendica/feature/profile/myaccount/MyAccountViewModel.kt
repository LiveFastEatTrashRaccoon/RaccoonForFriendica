package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.EntryActionHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.LogoutUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
@OptIn(FlowPreview::class)
class MyAccountViewModel(
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val entryActionHelper: EntryActionHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val logout: LogoutUseCase,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<MyAccountMvi.Intent, MyAccountMvi.State, MyAccountMvi.Effect>
    by DefaultMviDelegate(initialState = MyAccountMvi.State()),
    MyAccountMvi {

    init {
        viewModelScope.launch {
            identityRepository
                .currentUser
                .debounce(750.milliseconds)
                .onEach { user ->
                    val currentUser =
                        user?.let {
                            with(emojiHelper) { it.withEmojisIfMissing() }
                        }
                    viewModelScope.launch {
                        loadData(currentUser)
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            blurNsfw = settings?.blurNsfw ?: true,
                            maxBodyLines = settings?.maxPostBodyLines ?: Int.MAX_VALUE,
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                            layout = settings?.timelineLayout ?: TimelineLayout.Full,
                        )
                    }
                }.launchIn(this)

            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    updateEntryInState(event.entry.id) { event.entry }
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryDeletedEvent::class)
                .onEach { event ->
                    removeEntryFromState(event.id)
                }.launchIn(this)
        }
    }

    private suspend fun loadData(currentUser: UserModel?) {
        if (currentUser != null) {
            updateState { it.copy(user = currentUser) }
            val initialValues = timelineEntryRepository.getCachedByUser().map { entry ->
                entry.let {
                    with(emojiHelper) { it.withEmojisIfMissing() }
                }.let {
                    with(replyHelper) { it.withInReplyToIfMissing() }
                }.let {
                    with(entryActionHelper) { it.withActions() }
                }
            }
            paginationManager.restoreHistory(initialValues)
            if (initialValues.isNotEmpty()) {
                updateState {
                    it.copy(
                        initial = false,
                        entries = initialValues,
                    )
                }
                paginationManager.reset(
                    TimelinePaginationSpecification.User(
                        userId = currentUser.id,
                        excludeReplies = true,
                        includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                        enableCache = true,
                    ),
                )
            } else {
                refresh(initial = true)
            }
        } else {
            // recover screen
            updateState {
                it.copy(initial = false)
            }
        }
    }

    override fun reduce(intent: MyAccountMvi.Intent) {
        when (intent) {
            is MyAccountMvi.Intent.ChangeSection -> handleChangeSection(intent.section)
            MyAccountMvi.Intent.LoadNextPage -> handleLoadNextPage()
            MyAccountMvi.Intent.Refresh -> handleRefresh()
            is MyAccountMvi.Intent.ToggleReblog -> handleToggleReblog(intent.entry)
            is MyAccountMvi.Intent.ToggleFavorite -> handleToggleFavorite(intent.entry)
            is MyAccountMvi.Intent.ToggleDislike -> handleToggleDislike(intent.entry)
            is MyAccountMvi.Intent.ToggleBookmark -> handleToggleBookmark(intent.entry)
            is MyAccountMvi.Intent.DeleteEntry -> handleDeleteEntry(intent.entryId)
            is MyAccountMvi.Intent.TogglePin -> handleTogglePin(intent.entry)
            is MyAccountMvi.Intent.CopyToClipboard -> handleCopyToClipboard(intent.entry)
            is MyAccountMvi.Intent.OpenInBrowser -> handleOpenInBrowser(intent.entry)
            MyAccountMvi.Intent.Logout -> handleLogout()
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            launch {
                val currentUser = userRepository.getCurrent(refresh = true)
                updateState { it.copy(user = currentUser) }
            }
            launch {
                refresh()
            }
        }
    }

    private fun handleLoadNextPage() {
        viewModelScope.launch {
            loadNextPage()
        }
    }

    private fun handleChangeSection(section: UserSection) {
        viewModelScope.launch {
            if (uiState.value.loading) {
                return@launch
            }
            updateState { it.copy(section = section) }
            emitEffect(MyAccountMvi.Effect.BackToTop)
            refresh(initial = true)
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            logout()
        }
    }

    private suspend fun refresh(initial: Boolean = false, forceRefresh: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val currentState = uiState.value
        val accountId =
            identityRepository.currentUser.value?.id ?: ""
        paginationManager.reset(
            TimelinePaginationSpecification.User(
                userId = accountId,
                excludeReplies = currentState.section == UserSection.Posts,
                onlyMedia = currentState.section == UserSection.Media,
                pinned = currentState.section == UserSection.Pinned,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                enableCache = currentState.section == UserSection.Posts,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        updateState { it.copy(loading = true) }
        try {
            val entries = paginationManager.loadNextPage().distinctBy { it.safeKey }
            entries.preloadImages()
            updateState {
                it.copy(
                    entries = entries,
                    canFetchMore = paginationManager.canFetchMore,
                    loading = false,
                    initial = false,
                    refreshing = false,
                )
            }
        } catch (e: Exception) {
            updateState { it.copy(loading = false, refreshing = false) }
            if (e is CancellationException) throw e
        }
    }

    private suspend fun List<TimelineEntryModel>.preloadImages() {
        flatMap { entry ->
            entry.original.urlsForPreload
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
        flatMap { entry ->
            entry.blurHashParamsForPreload
        }.forEach {
            blurHashRepository.preload(it)
        }
    }

    private suspend fun updateEntryInState(entryId: String, block: (TimelineEntryModel) -> TimelineEntryModel) {
        updateState {
            it.copy(
                entries =
                it.entries.map { entry ->
                    when {
                        entry.id == entryId -> {
                            entry.let(block)
                        }

                        entry.reblog?.id == entryId -> {
                            entry.copy(reblog = entry.reblog?.let(block))
                        }

                        else -> {
                            entry
                        }
                    }
                },
            )
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                entries = it.entries.filter { e -> e.id != entryId && e.reblog?.id != entryId },
            )
        }
    }

    private fun handleToggleReblog(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    reblogLoading = true,
                )
            }
            val newEntry =
                if (entry.reblogged) {
                    timelineEntryRepository.unreblog(entry.id)
                } else {
                    timelineEntryRepository.reblog(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it
                        .copy(
                            reblogged = newEntry.reblogged,
                            reblogCount = newEntry.reblogCount,
                            reblogLoading = false,
                        ).also { entry ->
                            notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                        }
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        reblogLoading = false,
                    )
                }
            }
        }
    }

    private fun handleToggleFavorite(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    favoriteLoading = true,
                )
            }
            val newEntry =
                toggleEntryFavorite(entry)?.also { e ->
                    notificationCenter.send(TimelineEntryUpdatedEvent(entry = e))
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    newEntry
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        favoriteLoading = false,
                    )
                }
            }
        }
    }

    private fun handleToggleDislike(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    dislikeLoading = true,
                )
            }
            val newEntry =
                toggleEntryDislike(entry)?.also { e ->
                    notificationCenter.send(TimelineEntryUpdatedEvent(entry = e))
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    newEntry
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        dislikeLoading = false,
                    )
                }
            }
        }
    }

    private fun handleToggleBookmark(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    bookmarkLoading = true,
                )
            }
            val newEntry =
                if (entry.bookmarked) {
                    timelineEntryRepository.unbookmark(entry.id)
                } else {
                    timelineEntryRepository.bookmark(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it
                        .copy(
                            bookmarked = newEntry.bookmarked,
                            bookmarkLoading = false,
                        ).also { entry ->
                            notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                        }
                }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(
                        bookmarkLoading = false,
                    )
                }
            }
        }
    }

    private fun handleDeleteEntry(entryId: String) {
        viewModelScope.launch {
            val success = timelineEntryRepository.delete(entryId)
            if (success) {
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            } else {
                emitEffect(MyAccountMvi.Effect.Failure)
            }
        }
    }

    private fun handleTogglePin(entry: TimelineEntryModel) {
        viewModelScope.launch {
            val newEntry =
                if (entry.pinned) {
                    timelineEntryRepository.unpin(entry.id)
                } else {
                    timelineEntryRepository.pin(entry.id)
                }
            if (newEntry != null) {
                if (uiState.value.section == UserSection.Pinned && !newEntry.pinned) {
                    removeEntryFromState(entry.id)
                } else {
                    updateEntryInState(entry.id) {
                        it.copy(
                            pinned = newEntry.pinned,
                        )
                    }
                }
            }
        }
    }

    private fun handleCopyToClipboard(entry: TimelineEntryModel) {
        viewModelScope.launch {
            val source = timelineEntryRepository.getSource(entry.id)
            if (source != null) {
                val text =
                    buildString {
                        if (!entry.title.isNullOrBlank()) {
                            append(entry.title)
                            append("\n")
                        }
                        append(source.content)
                    }
                emitEffect(MyAccountMvi.Effect.TriggerCopy(text))
            }
        }
    }

    private fun handleOpenInBrowser(entry: TimelineEntryModel) {
        viewModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(MyAccountMvi.Effect.OpenUrl(url))
            }
        }
    }
}
