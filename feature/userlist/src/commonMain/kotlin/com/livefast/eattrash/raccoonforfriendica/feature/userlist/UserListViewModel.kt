package com.livefast.eattrash.raccoonforfriendica.feature.userlist

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class UserListViewModel(
    private val type: UserListType,
    private val paginationManager: UserPaginationManager,
    private val userRepository: UserRepository,
    private val hapticFeedback: HapticFeedback,
    private val notificationCenter: NotificationCenter,
) : DefaultMviModel<UserListMviModel.Intent, UserListMviModel.State, UserListMviModel.Effect>(
        initialState = UserListMviModel.State(),
    ),
    UserListMviModel {
    init {
        screenModelScope.launch {
            notificationCenter
                .subscribe(UserUpdatedEvent::class)
                .onEach { event ->
                    updateUserInState(event.user.id) { event.user }
                }.launchIn(this)
            if (uiState.value.initial) {
                loadUser()
                refresh(initial = true)
            }
        }
    }

    private suspend fun loadUser() {
        val userId =
            when (type) {
                is UserListType.Follower -> type.userId
                is UserListType.Following -> type.userId
                else -> null
            }
        if (userId != null) {
            val user = userRepository.getById(id = userId)
            updateState {
                it.copy(user = user)
            }
        }
    }

    override fun reduce(intent: UserListMviModel.Intent) {
        when (intent) {
            UserListMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            UserListMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }
            is UserListMviModel.Intent.Follow -> follow(intent.userId)
            is UserListMviModel.Intent.Unfollow -> unfollow(intent.userId)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (type) {
                is UserListType.Follower -> UserPaginationSpecification.Follower(type.userId)
                is UserListType.Following -> UserPaginationSpecification.Following(type.userId)
                is UserListType.UsersFavorite -> UserPaginationSpecification.EntryUsersFavorite(type.entryId)
                is UserListType.UsersReblog -> UserPaginationSpecification.EntryUsersReblog(type.entryId)
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

        updateState { it.copy(loading = true) }
        val users = paginationManager.loadNextPage()
        updateState {
            it.copy(
                users = users,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
    }

    private suspend fun updateUserInState(
        userId: String,
        block: (UserModel) -> UserModel,
    ) {
        updateState {
            it.copy(
                users =
                    it.users.map { user ->
                        if (user.id == userId) {
                            user.let(block)
                        } else {
                            user
                        }
                    },
            )
        }
    }

    private fun follow(userId: String) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser = uiState.value.users.firstOrNull { it.id == userId }
            val newRelationship = userRepository.follow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it
                    .copy(
                        relationshipStatus = newStatus,
                        notificationStatus = newNotificationStatus,
                        relationshipStatusPending = false,
                    ).also { user ->
                        notificationCenter.send(UserUpdatedEvent(user = user))
                    }
            }
        }
    }

    private fun unfollow(userId: String) {
        hapticFeedback.vibrate()
        screenModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser = uiState.value.users.firstOrNull { it.id == userId }
            val newRelationship = userRepository.unfollow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it
                    .copy(
                        relationshipStatus = newStatus,
                        notificationStatus = newNotificationStatus,
                        relationshipStatusPending = false,
                    ).also { user ->
                        notificationCenter.send(UserUpdatedEvent(user = user))
                    }
            }
        }
    }
}
