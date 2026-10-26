package com.livefast.eattrash.raccoonforfriendica.feature.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserListUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactoryKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AssistedInject
class UserListViewModel(
    @Assisted private val type: UserListType,
    @Assisted private val userId: String?,
    @Assisted private val postId: String?,
    private val paginationManager: UserPaginationManager,
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val userCache: LocalItemCache<UserModel>,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    private val exportUserList: ExportUserListUseCase,
    private val notificationCenter: NotificationCenter,
) : ViewModel(),
    MviDelegate<UserListMvi.Intent, UserListMvi.State, UserListMvi.Effect>
    by DefaultMviDelegate(initialState = UserListMvi.State()),
    UserListMvi {

    init {
        viewModelScope.launch {
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
                        viewModelScope.launch {
                            refresh(initial = true)
                        }
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
                is UserListType.Follower -> userId
                is UserListType.Following -> userId
                else -> null
            }
        if (userId != null) {
            val user = userCache.get(userId)
            updateState {
                it.copy(user = user)
            }
        }
    }

    override fun reduce(intent: UserListMvi.Intent) {
        when (intent) {
            UserListMvi.Intent.LoadNextPage -> handleLoadNextPage()
            UserListMvi.Intent.Refresh -> handleRefresh()
            is UserListMvi.Intent.Follow -> handleFollow(intent.userId)
            is UserListMvi.Intent.Unfollow -> handleUnfollow(intent.userId)
            UserListMvi.Intent.Export -> handleExport()
        }
    }

    private fun handleLoadNextPage() {
        viewModelScope.launch {
            loadNextPage()
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            refresh()
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }
        paginationManager.reset(
            when (type) {
                is UserListType.Follower if !userId.isNullOrEmpty() -> UserPaginationSpecification.Follower(userId)

                is UserListType.Following if !userId.isNullOrEmpty() -> UserPaginationSpecification.Following(userId)

                is UserListType.UsersFavorite if !postId.isNullOrEmpty() ->
                    UserPaginationSpecification.PostUsersFavorite(postId)

                is UserListType.UsersReblog if !postId.isNullOrEmpty() ->
                    UserPaginationSpecification.PostUsersReblog(postId)

                else -> error("Invalid configuration")
            },
        )
        loadNextPage()
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) return

        val currentState = uiState.value
        updateState { it.copy(loading = true) }
        try {
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
                emitEffect(UserListMvi.Effect.BackToTop)
            }
        } catch (e: Exception) {
            updateState { it.copy(loading = false, refreshing = false) }
            if (e is CancellationException) throw e
        }
    }

    private fun List<UserModel>.preloadImages() {
        mapNotNull { user ->
            user.avatar?.takeIf { it.isNotEmpty() }
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
    }

    private suspend fun updateUserInState(userId: String, block: (UserModel) -> UserModel) {
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

    private fun handleFollow(userId: String) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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

    private fun handleUnfollow(userId: String) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
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
                UserListType.Follower if !userId.isNullOrEmpty() -> ExportUserSpecification.Follower(userId)
                UserListType.Following if !userId.isNullOrEmpty() -> ExportUserSpecification.Following(userId)
                UserListType.UsersFavorite -> null
                UserListType.UsersReblog -> null
                else -> error("Invalid configuration")
            } ?: return
        viewModelScope.launch {
            updateState { it.copy(operationInProgress = true) }
            val content = exportUserList(specification)
            updateState { it.copy(operationInProgress = false) }
            emitEffect(UserListMvi.Effect.SaveList(content))
        }
    }

    companion object {
        fun getExtras(type: UserListType, userId: String?, postId: String?) = CreationExtras {
            this[KEY_TYPE] = type
            this[KEY_USER_ID] = userId
            this[KEY_POST_ID] = postId
        }
    }
}

private val KEY_TYPE = CreationExtras.Key<UserListType>()
private val KEY_USER_ID = CreationExtras.Key<String?>()
private val KEY_POST_ID = CreationExtras.Key<String?>()

@AssistedFactory
@ViewModelAssistedFactoryKey(UserListViewModel::class)
@ContributesIntoMap(AppScope::class)
fun interface Factory : ViewModelAssistedFactory {
    override fun create(extras: CreationExtras): UserListViewModel = create(
        type = extras[KEY_TYPE] ?: error("type argument not found"),
        userId = extras[KEY_USER_ID],
        postId = extras[KEY_POST_ID],
    )

    fun create(@Assisted type: UserListType, @Assisted userId: String?, @Assisted postId: String?): UserListViewModel
}
