package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration

class TimelineViewModel(
    private val paginationManager: TimelinePaginationManager,
    private val identityRepository: IdentityRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
    private val circlesRepository: CirclesRepository,
    private val hapticFeedback: HapticFeedback,
    private val notificationCenter: NotificationCenter,
) : DefaultMviModel<TimelineMviModel.Intent, TimelineMviModel.State, TimelineMviModel.Effect>(
        initialState = TimelineMviModel.State(),
    ),
    TimelineMviModel {
    init {
        screenModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            timelineType = settings?.defaultTimelineType?.toTimelineType(),
                            blurNsfw = settings?.blurNsfw ?: true,
                        )
                    }
                }.launchIn(this)
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState { it.copy(currentUserId = currentUser?.id) }
                    refreshCirclesInTimelineTypes(currentUser != null)
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    updateEntryInState(event.entry.id) { event.entry }
                }.launchIn(this)

            combine(
                settingsRepository.current,
                identityRepository.currentUser,
            ) { _, user ->
                // wait until either there is a logged user if there are valid credentials stored
                if (user != null || !apiConfigurationRepository.hasCachedAuthCredentials()) {
                    refresh(initial = true)
                }
            }.launchIn(this)
        }
    }

    override fun reduce(intent: TimelineMviModel.Intent) {
        when (intent) {
            TimelineMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            TimelineMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            is TimelineMviModel.Intent.ChangeType ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }

                    updateState {
                        it.copy(timelineType = intent.type, initial = true)
                    }
                    emitEffect(TimelineMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            is TimelineMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is TimelineMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is TimelineMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            is TimelineMviModel.Intent.DeleteEntry -> deleteEntry(intent.entryId)
            is TimelineMviModel.Intent.MuteUser ->
                mute(
                    userId = intent.userId,
                    entryId = intent.entryId,
                    duration = intent.duration,
                    disableNotifications = intent.disableNotifications,
                )
            is TimelineMviModel.Intent.BlockUser ->
                block(
                    userId = intent.userId,
                    entryId = intent.entryId,
                )
            is TimelineMviModel.Intent.TogglePin -> togglePin(intent.entry)
            is TimelineMviModel.Intent.SubmitPollVote ->
                submitPoll(
                    intent.entry,
                    intent.choices,
                )

            is TimelineMviModel.Intent.ToggleSpoilerActive -> toggleSpoiler(intent.entry)
        }
    }

    private suspend fun refreshCirclesInTimelineTypes(isLogged: Boolean) {
        val circles = circlesRepository.getAll()
        val settings = settingsRepository.current.value
        val defaultTimelineTypes =
            buildList {
                this += TimelineType.All
                if (isLogged) {
                    this += TimelineType.Subscriptions
                }
                this += TimelineType.Local
            }
        val newTimelineTypes =
            defaultTimelineTypes + circles.map { TimelineType.Circle(it.id, it.name) }
        val currentTimelineType = uiState.value.timelineType
        val newTimelineType =
            if (currentTimelineType is TimelineType.Circle) {
                val newCircle = circles.firstOrNull { it.id == currentTimelineType.id }
                if (newCircle == null) {
                    // circle has been deleted
                    settings?.defaultTimelineType?.toTimelineType()
                } else {
                    // circle has been renamed
                    TimelineType.Circle(id = currentTimelineType.id, name = newCircle.name)
                }
            } else {
                currentTimelineType
            }
        updateState {
            it.copy(
                availableTimelineTypes = newTimelineTypes,
                timelineType = newTimelineType,
            )
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        // needed as a last-resort to update circles if edited elsewhere
        val isLogged = identityRepository.currentUser.value != null
        refreshCirclesInTimelineTypes(isLogged)
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.Feed(
                timelineType = uiState.value.timelineType ?: TimelineType.Local,
                includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
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
            emitEffect(TimelineMviModel.Effect.BackToTop)
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
                    it.copy(
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
                    it.copy(
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
                    it.copy(
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
                emitEffect(TimelineMviModel.Effect.PollVoteFailure)
            }
        }
    }

    private fun toggleSpoiler(entry: TimelineEntryModel) {
        screenModelScope.launch {
            updateEntryInState(entry.id) { entry.copy(isSpoilerActive = !entry.isSpoilerActive) }
        }
    }
}
