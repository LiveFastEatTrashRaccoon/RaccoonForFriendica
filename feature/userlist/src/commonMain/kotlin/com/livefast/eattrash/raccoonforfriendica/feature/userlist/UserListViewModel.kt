package com.livefast.eattrash.raccoonforfriendica.feature.userlist

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserListUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class UserListViewModel(
    private val type: UserListType,
    private val userId: String,
    private val entryId: String,
    private val paginationManager: UserPaginationManager,
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val userCache: LocalItemCache<UserModel>,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val exportUserList: ExportUserListUseCase,
    private val notificationCenter: NotificationCenter = getNotificationCenter(),
) : DefaultMviModel<UserListMviModel.Intent, UserListMviModel.State, UserListMviModel.Effect>(
        initialState = UserListMviModel.State(),
    ),
    UserListMviModel {
    init {
        screenModelScope.launch {
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
                    updateState {
                        it.copy(currentUserId = currentUser?.id)
                    }
                    if (uiState.value.initial) {
                        loadUser()
                        refresh(initial = true)
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                                settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            notificationCenter
                .subscribe(UserUpdatedEvent::class)
                .onEach { event ->
                    updateUserInState(event.user.id) { event.user }
                }.launchIn(this)
        }
    }

    private suspend fun loadUser() {
        val userId =
            when (type) {
                is UserListType.Follower -> userId.takeIf { it.isNotEmpty() }
                is UserListType.Following -> userId.takeIf { it.isNotEmpty() }
                else -> null
            }
        if (userId != null) {
            val user = userCache.get(userId)
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
            UserListMviModel.Intent.Export -> handleExport()
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (type) {
                is UserListType.Follower -> UserPaginationSpecification.Follower(userId)
                is UserListType.Following -> UserPaginationSpecification.Following(userId)
                is UserListType.UsersFavorite ->
                    UserPaginationSpecification.EntryUsersFavorite(entryId)

                is UserListType.UsersReblog -> UserPaginationSpecification.EntryUsersReblog(entryId)
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }

        val currentState = uiState.value
        updateState { it.copy(loading = true) }
        val users =
            paginationManager
                .loadNextPage()
                .map { user ->
                    if (currentState.currentUserId == null) {
                        user.copy(relationshipStatus = null)
                    } else if (user.id == currentState.currentUserId) {
                        user.copy(relationshipStatus = null)
                    } else {
                        user
                    }
                }
        users.preloadImages()
        val wasRefreshing = currentState.refreshing
        updateState {
            it.copy(
                users = users,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
        if (wasRefreshing) {
            emitEffect(UserListMviModel.Effect.BackToTop)
        }
    }

    private fun List<UserModel>.preloadImages() {
        mapNotNull { user ->
            user.avatar?.takeIf { it.isNotEmpty() }
        }.onEach { url ->
            imagePreloadManager.preload(url)
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

    private fun handleExport() {
        val specification =
            when (type) {
                UserListType.Follower -> ExportUserSpecification.Follower(userId)
                UserListType.Following -> ExportUserSpecification.Following(userId)
                UserListType.UsersFavorite -> null
                UserListType.UsersReblog -> null
            } ?: return
        screenModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val content = exportUserList(specification)
            updateState { it.copy(operationInProgress = false) }
            emitEffect(UserListMviModel.Effect.SaveList(content))
        }
    }
}
