package com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MyAccountViewModel(
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val notificationCenter: NotificationCenter,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
) : DefaultMviModel<MyAccountMviModel.Intent, MyAccountMviModel.State, MyAccountMviModel.Effect>(
        initialState = MyAccountMviModel.State(),
    ),
    MyAccountMviModel {
    init {
        screenModelScope.launch {
            identityRepository
                .currentUser
                .drop(1)
                .distinctUntilChanged()
                .debounce(750)
                .onEach { user ->
                    val currentUser =
                        user?.let {
                            with(emojiHelper) { it.withEmojisIfMissing() }
                        }
                    updateState {
                        it.copy(user = currentUser)
                    }
                    refresh(initial = true)
                }.launchIn(this)
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            blurNsfw = settings?.blurNsfw ?: true,
                            maxBodyLines = settings?.maxPostBodyLines ?: Int.MAX_VALUE,
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

            val currentUser =
                identityRepository.currentUser.value?.let {
                    with(emojiHelper) { it.withEmojisIfMissing() }
                }
            updateState {
                it.copy(user = currentUser)
            }

            if (uiState.value.initial) {

                val initialValues =
                    timelineEntryRepository
                        .getCachedByUser()
                        .map {
                            with(emojiHelper) { it.withEmojisIfMissing() }
                        }.map {
                            with(replyHelper) { it.withInReplyToIfMissing() }
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
                            userId = currentUser?.id.orEmpty(),
                            excludeReplies = true,
                            includeNsfw =
                                settingsRepository.current.value?.includeNsfw
                                    ?: false,
                            enableCache = true,
                        ),
                    )
                } else {
                    refresh(initial = true)
                }
            }
        }
    }

    override fun reduce(intent: MyAccountMviModel.Intent) {
        when (intent) {
            is MyAccountMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(MyAccountMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            MyAccountMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            MyAccountMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    launch {
                        val currentUser = userRepository.getCurrent(refresh = true)
                        updateState { it.copy(user = currentUser) }
                    }
                    launch {
                        refresh()
                    }
                }

            is MyAccountMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is MyAccountMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is MyAccountMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is MyAccountMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is MyAccountMviModel.Intent.TogglePin -> togglePin(intent.entry)
            is MyAccountMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
        }
    }

    private suspend fun refresh(
        initial: Boolean = false,
        forceRefresh: Boolean = false,
    ) {
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
                includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
                enableCache = currentState.section == UserSection.Posts,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val entries = paginationManager.loadNextPage()
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

    private suspend fun updateEntryInState(
        entryId: String,
        block: (TimelineEntryModel) -> TimelineEntryModel,
    ) {
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
        screenModelScope.launch {
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
        screenModelScope.launch {
            updateEntryInState(entry.id) {
                it.copy(
                    favoriteLoading = true,
                )
            }
            val newEntry =
                if (entry.favorite) {
                    timelineEntryRepository.unfavorite(entry.id)
                } else {
                    timelineEntryRepository.favorite(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it
                        .copy(
                            favorite = newEntry.favorite,
                            favoriteCount = newEntry.favoriteCount,
                            favoriteLoading = false,
                        ).also { entry ->
                            notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                        }
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

    private fun toggleBookmark(entry: TimelineEntryModel) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
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
        screenModelScope.launch {
            val success = timelineEntryRepository.delete(entryId)
            if (success) {
                notificationCenter.send(TimelineEntryDeletedEvent(entryId))
                removeEntryFromState(entryId)
            } else {
                emitEffect(MyAccountMviModel.Effect.Failure)
            }
        }
    }

    private fun togglePin(entry: TimelineEntryModel) {
        screenModelScope.launch {
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

    private fun copyToClipboard(entry: TimelineEntryModel) {
        screenModelScope.launch {
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
                emitEffect(MyAccountMviModel.Effect.TriggerCopy(text))
            }
        }
    }
}
