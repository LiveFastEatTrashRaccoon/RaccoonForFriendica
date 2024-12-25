package com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class CircleTimelineViewModel(
    id: String,
    private val paginationManager: TimelinePaginationManager,
    private val identityRepository: IdentityRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val circleCache: LocalItemCache<CircleModel>,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val toggleEntryDislike: ToggleEntryDislikeUseCase,
    private val toggleEntryFavorite: ToggleEntryFavoriteUseCase,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<CircleTimelineMviModel.Intent, CircleTimelineMviModel.State, CircleTimelineMviModel.Effect>(
        initialState = CircleTimelineMviModel.State(),
    ),
    CircleTimelineMviModel {
    init {
        screenModelScope.launch {
            val circle = circleCache.get(id)
            updateState { it.copy(circle = circle) }

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

            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                }.launchIn(this)

            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    updateEntryInState(event.entry.id) { event.entry }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(
                    initial = true,
                    forceRefresh = true,
                )
            }
        }
    }

    override fun reduce(intent: CircleTimelineMviModel.Intent) {
        when (intent) {
            CircleTimelineMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            CircleTimelineMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is CircleTimelineMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is CircleTimelineMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is CircleTimelineMviModel.Intent.ToggleDislike -> toggleDislike(intent.entry)
            is CircleTimelineMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is CircleTimelineMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is CircleTimelineMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )

            is CircleTimelineMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )

            is CircleTimelineMviModel.Intent.TogglePin -> togglePin(intent.entry)
            is CircleTimelineMviModel.Intent.SubmitPollVote ->
                submitPoll(
                    intent.entry,
                    intent.choices,
                )

            is CircleTimelineMviModel.Intent.CopyToClipboard -> copyToClipboard(intent.entry)
        }
    }

    private suspend fun refresh(
        initial: Boolean = false,
        forceRefresh: Boolean = false,
    ) {
        val circle = uiState.value.circle ?: return
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        val settings = settingsRepository.current.value ?: SettingsModel()
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = TimelineType.Circle(circle),
                includeNsfw = settings.includeNsfw,
                excludeReplies = settings.excludeRepliesFromTimeline,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        val wasRefreshing = uiState.value.refreshing
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
        if (wasRefreshing) {
            emitEffect(CircleTimelineMviModel.Effect.BackToTop)
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
        screenModelScope.launch {
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

    private fun togglePin(entry: TimelineEntryModel) {
        screenModelScope.launch {
            val newEntry =
                if (entry.pinned) {
                    timelineEntryRepository.unpin(entry.id)
                } else {
                    timelineEntryRepository.pin(entry.id)
                }
            if (newEntry != null) {
                updateEntryInState(entry.id) {
                    it.copy(
                        pinned = newEntry.pinned,
                    )
                }
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
                updateEntryInState(entry.id) {
                    it.copy(poll = newPoll).also { entry ->
                        notificationCenter.send(TimelineEntryUpdatedEvent(entry = entry))
                    }
                }
            } else {
                updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(CircleTimelineMviModel.Effect.PollVoteFailure)
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
                emitEffect(CircleTimelineMviModel.Effect.TriggerCopy(text))
            }
        }
    }
}
