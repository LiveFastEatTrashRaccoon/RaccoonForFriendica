package com.livefast.eattrash.feature.userdetail.classic

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val id: String,
    private val userRepository: UserRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
) : DefaultMviModel<UserDetailMviModel.Intent, UserDetailMviModel.State, UserDetailMviModel.Effect>(
        initialState = UserDetailMviModel.State(),
    ),
    UserDetailMviModel {
    init {
        screenModelScope.launch {
            identityRepository.currentUser
                .onEach { user ->
                    updateState { it.copy(currentUserId = user?.id) }
                    loadUser()
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState { it.copy(blurNsfw = settings?.blurNsfw ?: true) }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: UserDetailMviModel.Intent) {
        when (intent) {
            is UserDetailMviModel.Intent.ChangeSection ->
                screenModelScope.launch {
                    if (uiState.value.loading) {
                        return@launch
                    }
                    updateState { it.copy(section = intent.section) }
                    emitEffect(UserDetailMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            UserDetailMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            UserDetailMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            UserDetailMviModel.Intent.AcceptFollowRequest -> acceptFollowRequest()
            UserDetailMviModel.Intent.Follow -> follow()
            UserDetailMviModel.Intent.Unfollow -> unfollow()
            is UserDetailMviModel.Intent.ToggleReblog -> toggleReblog(intent.entry)
            is UserDetailMviModel.Intent.ToggleFavorite -> toggleFavorite(intent.entry)
            is UserDetailMviModel.Intent.ToggleBookmark -> toggleBookmark(intent.entry)
            UserDetailMviModel.Intent.DisableNotifications -> toggleNotifications(false)
            UserDetailMviModel.Intent.EnableNotifications -> toggleNotifications(true)
            is UserDetailMviModel.Intent.SubmitPollVote -> submitPoll(intent.entry, intent.choices)
        }
    }

    private suspend fun loadUser() {
        val user = userRepository.getById(id)
        val relationship =
            if (id != uiState.value.currentUserId) {
                userRepository.getRelationships(listOf(id)).firstOrNull()
            } else {
                null
            }
        updateState {
            it.copy(
                user =
                    user?.copy(
                        relationshipStatus = relationship?.toStatus(),
                        notificationStatus = relationship?.toNotificationStatus(),
                    ),
            )
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            TimelinePaginationSpecification.User(
                userId = id,
                excludeReplies = uiState.value.section == UserSection.Posts,
                onlyMedia = uiState.value.section == UserSection.Media,
                pinned = uiState.value.section == UserSection.Pinned,
                includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
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

    private fun acceptFollowRequest() {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(relationshipStatusPending = true)) }
            userRepository.acceptFollowRequest(id)
            val newRelationship = userRepository.getRelationships(listOf(id)).firstOrNull()
            val newStatus = newRelationship?.toStatus() ?: uiState.value.user?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user?.copy(
                            relationshipStatus = newStatus,
                            notificationStatus = newNotificationStatus,
                            relationshipStatusPending = false,
                        ),
                )
            }
        }
    }

    private fun follow() {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(relationshipStatusPending = true)) }
            val newRelationship = userRepository.follow(id)
            val newStatus = newRelationship?.toStatus() ?: uiState.value.user?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user?.copy(
                            relationshipStatus = newStatus,
                            notificationStatus = newNotificationStatus,
                            relationshipStatusPending = false,
                        ),
                )
            }
        }
    }

    private fun unfollow() {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(relationshipStatusPending = true)) }
            val newRelationship = userRepository.unfollow(id)
            val newStatus = newRelationship?.toStatus() ?: uiState.value.user?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user?.copy(
                            relationshipStatus = newStatus,
                            notificationStatus = newNotificationStatus,
                            relationshipStatusPending = false,
                        ),
                )
            }
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
                    )
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
                    )
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
                    )
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

    private fun toggleNotifications(enabled: Boolean) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateState { it.copy(user = it.user?.copy(notificationStatusPending = true)) }
            val newRelationship =
                userRepository.follow(
                    id = id,
                    notifications = enabled,
                )
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: uiState.value.user?.notificationStatus
            updateState {
                it.copy(
                    user =
                        it.user?.copy(
                            notificationStatus = newNotificationStatus,
                            notificationStatusPending = false,
                        ),
                )
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
                updateEntryInState(entry.id) { it.copy(poll = poll.copy(loading = false)) }
                emitEffect(UserDetailMviModel.Effect.PollVoteFailure)
            }
        }
    }
}
