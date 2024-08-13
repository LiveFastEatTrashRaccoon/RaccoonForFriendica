package com.livefast.eattrash.feature.userdetail

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val id: String,
    private val userRepository: UserRepository,
    private val paginationManager: TimelinePaginationManager,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val accountRepository: AccountRepository,
) : DefaultMviModel<UserDetailMviModel.Intent, UserDetailMviModel.State, UserDetailMviModel.Effect>(
        initialState = UserDetailMviModel.State(),
    ),
    UserDetailMviModel {
    init {
        screenModelScope.launch {
            apiConfigurationRepository.isLogged
                .onEach {
                    val currentUserHandle = accountRepository.getActive()?.handle.orEmpty()
                    val currentUser = userRepository.getByHandle(currentUserHandle)
                    updateState { it.copy(currentUserId = currentUser?.id) }

                    loadUser()
                    refresh(initial = true)
                }.launchIn(this)
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
        }
    }

    private suspend fun loadUser() {
        val account = userRepository.getById(id)
        val relationship =
            if (id != uiState.value.currentUserId) {
                userRepository.getRelationships(listOf(id)).firstOrNull()
            } else {
                null
        }
        updateState {
            it.copy(
                user =
                    account?.copy(
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
        screenModelScope.launch {
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
                    )
                }
            }
        }
    }

    private fun toggleFavorite(entry: TimelineEntryModel) {
        screenModelScope.launch {
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
                    )
                }
            }
        }
    }

    private fun toggleBookmark(entry: TimelineEntryModel) {
        screenModelScope.launch {
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
                    )
                }
            }
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
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
}
