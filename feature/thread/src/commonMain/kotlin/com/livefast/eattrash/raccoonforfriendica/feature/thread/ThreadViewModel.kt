package com.livefast.eattrash.raccoonforfriendica.feature.thread

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase.PopulateThreadUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class ThreadViewModel(
    private val entryId: String,
    private val populateThreadUseCase: PopulateThreadUseCase,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val hapticFeedback: HapticFeedback,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
    private val notificationCenter: NotificationCenter,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : DefaultMviModel<ThreadMviModel.Intent, ThreadMviModel.State, ThreadMviModel.Effect>(
        initialState = ThreadMviModel.State(),
    ),
    ThreadMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            blurNsfw = settings?.blurNsfw ?: true,
                            hideNavigationBarWhileScrolling =
                                settings?.hideNavigationBarWhileScrolling ?: true,
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

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ThreadMviModel.Intent) {
        when (intent) {
            ThreadMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is ThreadMviModel.Intent.LoadMoreReplies ->
                screenModelScope.launch {
                    loadMoreReplies(intent.entry)
                }

            is ThreadMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is ThreadMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is ThreadMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is ThreadMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is ThreadMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            is ThreadMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
            is ThreadMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
            is ThreadMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        val originalEntry = entryCache.get(entryId)
        updateState {
            it.copy(
                entry = originalEntry,
                initial = initial,
                loading = true,
                refreshing = !initial,
                canFetchMore = true,
            )
        }
        val result = populateThreadUseCase(entryId)
        val replies = result.filter { it.id != entryId }
        replies.preloadImages()
        updateState {
            it.copy(
                replies = replies,
                loading = false,
                initial = false,
                refreshing = false,
                canFetchMore = false,
            )
        }
    }

    private suspend fun loadMoreReplies(entry: TimelineEntryModel) {
        val result = populateThreadUseCase(entry.id)
        val newReplies = result.filter { it.id != entry.id }
        if (newReplies.isEmpty()) {
            // abort and disable load more button
            updateEntryInState(entry.id) { it.copy(loadMoreButtonVisible = false) }
        } else {
            newReplies.preloadImages()
            val replies = uiState.value.replies.toMutableList()
            val insertIndex = replies.indexOfFirst { it.id == entry.id }
            replies[insertIndex] = replies[insertIndex].copy(loadMoreButtonVisible = false)
            replies.addAll(index = insertIndex + 1, newReplies)
            updateState {
                it.copy(replies = replies)
            }
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
        val currentMainEntry = uiState.value.entry
        when (entryId) {
            currentMainEntry?.id -> {
                updateState {
                    it.copy(entry = currentMainEntry.let(block))
                }
            }

            currentMainEntry?.reblog?.id -> {
                updateState {
                    it.copy(
                        entry = currentMainEntry.copy(reblog = currentMainEntry.reblog?.let(block)),
                    )
                }
            }

            else -> {
                updateState {
                    it.copy(
                        replies =
                            it.replies.map { entry ->
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
        }
    }

    private suspend fun removeEntryFromState(entryId: String) {
        updateState {
            it.copy(
                replies = it.replies.filter { e -> e.id != entryId && e.reblog?.id != entryId },
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
            }
        }
    }

    private fun mute(
        userId: String,
        entryId: String,
        duration: Duration,
        disableNotifications: Boolean,
    ) {
        screenModelScope.launch {
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

    private fun block(
        userId: String,
        entryId: String,
    ) {
        screenModelScope.launch {
            val res = userRepository.block(userId)
            if (res != null) {
                removeEntryFromState(entryId)
            }
        }
    }

    private fun submitPoll(
        entry: TimelineEntryModel,
        choices: List<Int>,
    ) {
        val poll = entry.poll ?: return
        screenModelScope.launch {
            updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = true)) }
            val newPoll =
                timelineEntryRepository.submitPoll(
                    pollId = poll.id,
                    choices = choices,
                )
            if (newPoll != null) {
                updateEntryInState(entry.id) { it.copy(poll = newPoll) }
            } else {
                updateEntryInState(entry.id) {
                    it.copy(poll = poll.copy(loading = false)).also { entry ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                    }
                }
                emitEffect(ThreadMviModel.Effect.PollVoteFailure)
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
                emitEffect(ThreadMviModel.Effect.TriggerCopy(text))
            }
        }
    }
}
