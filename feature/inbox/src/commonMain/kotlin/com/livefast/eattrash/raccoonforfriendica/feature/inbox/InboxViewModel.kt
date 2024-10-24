package com.livefast.eattrash.raccoonforfriendica.feature.inbox

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.ImagePreloadManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.vibrate.HapticFeedback
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications.PullNotificationChecker
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
    private val pullNotificationChecker: PullNotificationChecker,
) : DefaultMviModel<InboxMviModel.Intent, InboxMviModel.State, InboxMviModel.Effect>(
        initialState = InboxMviModel.State(),
    ),
    InboxMviModel {
    init {
        screenModelScope.launch {
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
                        )
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: InboxMviModel.Intent) {
        when (intent) {
            InboxMviModel.Intent.LoadNextPage ->
                screenModelScope.launch {
                    loadNextPage()
                }

            InboxMviModel.Intent.Refresh ->
                screenModelScope.launch {
                    refresh()
                }

            is InboxMviModel.Intent.ChangeSelectedNotificationTypes ->
                screenModelScope.launch {
                    updateState {
                        it.copy(selectedNotificationTypes = intent.types, initial = true)
                    }
                    emitEffect(InboxMviModel.Effect.BackToTop)
                    refresh(initial = true)
                }

            is InboxMviModel.Intent.Follow -> follow(intent.userId)
            is InboxMviModel.Intent.Unfollow -> unfollow(intent.userId)
            is InboxMviModel.Intent.MarkAsRead -> markAsRead(intent.notification)
            InboxMviModel.Intent.DismissAll -> dismissAll()
            is InboxMviModel.Intent.Dismiss -> dismiss(intent.notification)
            is InboxMviModel.Intent.ToggleSpoilerActive -> toggleSpoiler(intent.entry)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(initial = initial, refreshing = !initial)
        }

        if (!initial) {
            updateLastPositionMarker()
        }

        paginationManager.reset(
            NotificationsPaginationSpecification.Default(
                types = uiState.value.selectedNotificationTypes,
                includeNsfw = settingsRepository.current.value?.includeNsfw ?: false,
                refresh = !initial,
            ),
        )
        loadNextPage()

        if (!initial) {
            inboxManager.refreshUnreadCount()
        }
    }

    private suspend fun loadNextPage() {
        if (uiState.value.loading) {
            return
        }

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

    private suspend fun updateUserInState(
        userId: String,
        block: (UserModel) -> UserModel,
    ) {
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

    private suspend fun updateEntryInState(
        userId: String,
        block: (TimelineEntryModel) -> TimelineEntryModel,
    ) {
        updateState {
            it.copy(
                notifications =
                    it.notifications.map { notification ->
                        if (notification.entry?.reblog?.id == userId) {
                            notification.copy(
                                entry =
                                    notification.entry?.copy(
                                        reblog =
                                            notification.entry?.reblog?.let(
                                                block,
                                            ),
                                    ),
                            )
                        } else if (notification.entry?.id == userId) {
                            notification.copy(
                                entry = notification.entry?.let(block),
                            )
                        } else {
                            notification
                        }
                    },
            )
        }
    }

    private suspend fun updateItemInState(
        id: String,
        block: (NotificationModel) -> NotificationModel,
    ) {
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
        screenModelScope.launch {
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
        screenModelScope.launch {
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
        pullNotificationChecker.cancelAll()
        notifications.forEach { n ->
            updateItemInState(n.id) { it.copy(read = true) }
        }
    }

    private fun markAsRead(notification: NotificationModel) {
        if (notification.read) {
            return
        }
        screenModelScope.launch {
            updateItemInState(notification.id) { it.copy(read = true) }
            inboxManager.decrementUnreadCount()
        }
    }

    private fun dismiss(notification: NotificationModel) {
        if (notification.read) {
            return
        }
        screenModelScope.launch {
            val success = notificationRepository.dismiss(notification.id)
            if (success) {
                updateItemInState(notification.id) { it.copy(read = true) }
                inboxManager.decrementUnreadCount()
            }
        }
    }

    private fun dismissAll() {
        screenModelScope.launch {
            updateState { it.copy(markAllAsReadLoading = true) }
            notificationRepository.dismissAll()
            updateState { it.copy(markAllAsReadLoading = false) }
            refresh()
        }
    }

    private fun toggleSpoiler(entry: TimelineEntryModel) {
        screenModelScope.launch {
            updateEntryInState(entry.id) { entry.copy(isSpoilerActive = !entry.isSpoilerActive) }
        }
    }
}
