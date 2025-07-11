package com.livefast.eattrash.raccoonforfriendica.feature.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.blurHashParamsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.hasPriorIdThen
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.urlsForPreload
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MarkerRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InboxViewModel(
    private val paginationManager: NotificationsPaginationManager,
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val inboxManager: InboxManager,
    private val hapticFeedback: HapticFeedback,
    private val imagePreloadManager: ImagePreloadManager,
    private val blurHashRepository: BlurHashRepository,
    private val markerRepository: MarkerRepository,
    private val pullNotificationManager: PullNotificationManager,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<InboxMviModel.Intent, InboxMviModel.State, InboxMviModel.Effect>
    by DefaultMviModelDelegate(initialState = InboxMviModel.State()),
    InboxMviModel {
    init {
        viewModelScope.launch {
            identityRepository.currentUser
                .onEach { currentUser ->
                    updateState {
                        it.copy(currentUserId = currentUser?.id)
                    }
                    refresh(initial = true)
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            blurNsfw = settings?.blurNsfw ?: true,
                            maxBodyLines = settings?.maxPostBodyLines ?: Int.MAX_VALUE,
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
        }
    }

    override fun reduce(intent: InboxMviModel.Intent) {
        when (intent) {
            InboxMviModel.Intent.LoadNextPage ->
                viewModelScope.launch {
                    loadNextPage()
                }

            InboxMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            is InboxMviModel.Intent.ChangeSelectedNotificationTypes ->
                viewModelScope.launch {
                    updateState {
                        it.copy(selectedNotificationTypes = intent.types, initial = true)
                    }
                    emitEffect(InboxMviModel.Effect.BackToTop)
                    refresh(initial = true, forceRefresh = true)
                }

            is InboxMviModel.Intent.Follow -> follow(intent.userId)
            is InboxMviModel.Intent.Unfollow -> unfollow(intent.userId)
            is InboxMviModel.Intent.MarkAsRead -> markAsRead(intent.notification)
            InboxMviModel.Intent.DismissAll -> dismissAll()
            is InboxMviModel.Intent.Dismiss -> dismiss(intent.notification)
        }
    }

    private suspend fun refresh(initial: Boolean = false, forceRefresh: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }

        if (!initial) {
            updateLastPositionMarker()
        }

        paginationManager.reset(
            NotificationsPaginationSpecification.Default(
                types = uiState.value.selectedNotificationTypes,
                includeNsfw = settingsRepository.current.value?.includeNsfw == true,
                refresh = forceRefresh || !initial,
            ),
        )
        loadNextPage()

        if (!initial) {
            inboxManager.refreshUnreadCount()
        }
    }

    private suspend fun loadNextPage() {
        check(!uiState.value.loading) { return }

        val lastReadId = markerRepository.get(MarkerType.Notifications)?.lastReadId
        val wasRefreshing = uiState.value.refreshing
        updateState { it.copy(loading = true) }
        val notifications =
            paginationManager
                .loadNextPage()
                .takeIf { uiState.value.currentUserId != null }
                .orEmpty()
                .map { it.copy(read = it.hasPriorIdThen(lastReadId)) }
        notifications.preloadImages()
        updateState {
            it.copy(
                notifications = notifications,
                canFetchMore = paginationManager.canFetchMore,
                loading = false,
                initial = false,
                refreshing = false,
            )
        }
        if (wasRefreshing) {
            emitEffect(InboxMviModel.Effect.BackToTop)
        }
    }

    private suspend fun List<NotificationModel>.preloadImages() {
        flatMap { notification ->
            notification.urlsForPreload
        }.forEach { url ->
            imagePreloadManager.preload(url)
        }
        flatMap { entry ->
            entry.blurHashParamsForPreload
        }.forEach {
            blurHashRepository.preload(it)
        }
    }

    private suspend fun updateUserInState(userId: String, block: (UserModel) -> UserModel) {
        updateState {
            it.copy(
                notifications =
                it.notifications.map { notification ->
                    if (notification.user?.id == userId) {
                        notification.copy(
                            user = notification.user?.let(block),
                        )
                    } else {
                        notification
                    }
                },
            )
        }
    }

    private suspend fun updateItemInState(id: String, block: (NotificationModel) -> NotificationModel) {
        updateState {
            it.copy(
                notifications =
                it.notifications.map { notification ->
                    if (notification.id == id) {
                        notification.let(block)
                    } else {
                        notification
                    }
                },
            )
        }
    }

    private fun follow(userId: String) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.notifications
                    .firstOrNull { it.user?.id == userId }
                    ?.user
            val newRelationship = userRepository.follow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it.copy(
                    relationshipStatus = newStatus,
                    notificationStatus = newNotificationStatus,
                    relationshipStatusPending = false,
                )
            }
        }
    }

    private fun unfollow(userId: String) {
        hapticFeedback.vibrate()
        viewModelScope.launch {
            updateUserInState(userId) { it.copy(relationshipStatusPending = true) }
            val currentUser =
                uiState.value.notifications
                    .firstOrNull { it.user?.id == userId }
                    ?.user
            val newRelationship = userRepository.unfollow(userId)
            val newStatus = newRelationship?.toStatus() ?: currentUser?.relationshipStatus
            val newNotificationStatus =
                newRelationship?.toNotificationStatus() ?: currentUser?.notificationStatus
            updateUserInState(userId) {
                it.copy(
                    relationshipStatus = newStatus,
                    notificationStatus = newNotificationStatus,
                    relationshipStatusPending = false,
                )
            }
        }
    }

    private suspend fun updateLastPositionMarker() {
        val notifications = uiState.value.notifications
        val mostRecentId = notifications.firstOrNull()?.id ?: return
        markerRepository.update(
            type = MarkerType.Notifications,
            id = mostRecentId,
        )
        inboxManager.refreshUnreadCount()
        pullNotificationManager.cancelAll()
        notifications.forEach { n ->
            updateItemInState(n.id) { it.copy(read = true) }
        }
    }

    private fun markAsRead(notification: NotificationModel) {
        check(!notification.read) { return }
        viewModelScope.launch {
            updateItemInState(notification.id) { it.copy(read = true) }
            inboxManager.decrementUnreadCount()
        }
    }

    private fun dismiss(notification: NotificationModel) {
        check(!notification.read) { return }
        viewModelScope.launch {
            val success = notificationRepository.dismiss(notification.id)
            if (success) {
                updateItemInState(notification.id) { it.copy(read = true) }
                inboxManager.decrementUnreadCount()
            }
        }
    }

    private fun dismissAll() {
        viewModelScope.launch {
            updateState { it.copy(markAllAsReadLoading = true) }
            notificationRepository.dismissAll()
            updateState { it.copy(markAllAsReadLoading = false) }
            refresh()
        }
    }
}
