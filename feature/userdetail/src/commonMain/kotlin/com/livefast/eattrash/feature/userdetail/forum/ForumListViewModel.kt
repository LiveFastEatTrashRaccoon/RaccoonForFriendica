package com.livefast.eattrash.feature.userdetail.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class ForumListViewModel(
    private val id: String,
    private val userRepository: UserRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
    private val instanceShortcutRepository: InstanceShortcutRepository,
    private val hapticFeedback: HapticFeedback,
    private val userCache: LocalItemCache<UserModel>,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val getTranslation: GetTranslationUseCase,
    private val getInnerUrl: GetInnerUrlUseCase,
    private val timelineNavigationManager: TimelineNavigationManager,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : ViewModel(),
    MviModelDelegate<ForumListMviModel.Intent, ForumListMviModel.State, ForumListMviModel.Effect>
    by DefaultMviModelDelegate(initialState = ForumListMviModel.State()),
    ForumListMviModel {
    init {
        viewModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
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
                            lang = settings?.lang,
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

            apiConfigurationRepository.node
                .onEach { node ->
                    updateState { it.copy(currentNode = node) }
                }.launchIn(this)

            if (uiState.value.initial) {
                loadUser()
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ForumListMviModel.Intent) {
        when (intent) {
            ForumListMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPage()
                }

            ForumListMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            is ForumListMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is ForumListMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is ForumListMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is ForumListMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is ForumListMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is ForumListMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            is ForumListMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )

            is ForumListMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
            is ForumListMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
            is ForumListMviModel.Intent.ToggleTranslation -> toggleTranslation(intent.entry)
            is ForumListMviModel.Intent.WillOpenDetail ->
                viewModelScope.launch {
                    val state = paginationManager.extractState()
                    timelineNavigationManager.push(state)
                    emitEffect(ForumListMviModel.Effect.OpenDetail(intent.entry))
                }
            is ForumListMviModel.Intent.AddInstanceShortcut -> addInstanceShortcut(intent.node)
            is ForumListMviModel.Intent.OpenInBrowser -> openInBrowser(intent.entry)
        }
    }

    private suspend fun loadUser() {
        val account = userCache.get(id)
        updateState {
            it.copy(user = account)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.Forum(
                userId = id,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }

        updateState { it.copy(loading = true) }
        val entries =
            run {
                // must wait until some content is fetched (due to replies being filtered out)
                var res: List<TimelineEntryModel> = emptyList()
                while (paginationManager.canFetchMore && res.isEmpty()) {
                    res = paginationManager.loadNextPage()
                }
                res
            }
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

    private fun toggleReblog(entry: TimelineEntryModel) {
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

    private fun toggleFavorite(entry: TimelineEntryModel) {
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

    private fun toggleDislike(entry: TimelineEntryModel) {
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

    private fun toggleBookmark(entry: TimelineEntryModel) {
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

    private fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            val success = timelineEntryRepository.delete(entryId)
            if (success) {
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            }
        }
    }

    private fun mute(userId: String, entryId: String, duration: Duration, disableNotifications: Boolean) {
        viewModelScope.launch {
            val res =
                userRepository.mute(
                    id = userId,
                    durationSeconds = if (duration.isInfinite()) 0 else duration.inWholeSeconds,
                    notifications = disableNotifications,
                )
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun block(userId: String, entryId: String) {
        viewModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun submitPoll(entry: TimelineEntryModel, choices: List<Int>) {
        val poll = entry.poll ?: return
        viewModelScope.launch {
            updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = true)) }
            val newPoll =
                timelineEntryRepository.submitPoll(
                    pollId = poll.id,
                    choices = choices,
                )
            if (newPoll != null) {
                updateEntryInState(entry.id) {
                    it.copy(poll = newPoll).also { entry ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                    }
                }
            } else {
                updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(ForumListMviModel.Effect.PollVoteFailure)
            }
        }
    }

    private fun copyToClipboard(entry: TimelineEntryModel) {
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
                emitEffect(ForumListMviModel.Effect.TriggerCopy(text))
            }
        }
    }

    private fun toggleTranslation(entry: TimelineEntryModel) {
        val targetLang = uiState.value.lang ?: return
        check(!entry.translationLoading) { return }

        viewModelScope.launch {
            updateEntryInState(entry.id) { entry.copy(translationLoading = true) }
            val isBeingTranslated = !entry.isShowingTranslation

            val (translation, provider) =
                when {
                    isBeingTranslated && entry.translation == null -> {
                        val result = getTranslation(entry = entry, targetLang = targetLang)
                        result?.target to result?.provider
                    }

                    isBeingTranslated -> entry.translation to entry.translationProvider

                    else -> entry to entry.translationProvider
                }
            val newEntry =
                entry.copy(
                    isShowingTranslation = isBeingTranslated,
                    translation = translation,
                    translationProvider = provider,
                    translationLoading = false,
                )
            updateEntryInState(entry.id) { newEntry }
        }
    }

    private fun addInstanceShortcut(nodeName: String) {
        viewModelScope.launch {
            accountRepository.getActive()?.id?.also { accountId ->
                instanceShortcutRepository.create(
                    accountId = accountId,
                    node = nodeName,
                )
            }
        }
    }

    private fun openInBrowser(entry: TimelineEntryModel) {
        viewModelScope.launch {
            val url = getInnerUrl(entry)
            if (url != null) {
                emitEffect(ForumListMviModel.Effect.OpenUrl(url))
            }
        }
    }
}
