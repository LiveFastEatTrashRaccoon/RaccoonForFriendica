package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

internal class DefaultInboxManager(
    private val notificationRepository: NotificationRepository,
) : InboxManager {
    override val unreadCount = MutableStateFlow(0)

    override suspend fun refreshUnreadCount() {
        val types =
            listOf(
                NotificationType.Follow,
                NotificationType.FollowRequest,
                NotificationType.Mention,
                NotificationType.Entry,
                NotificationType.Update,
            )
        val notifications = notificationRepository.getAll(types)
        unreadCount.update {
            notifications?.count { !it.read } ?: 0
        }
    }

    override suspend fun decrementUnreadCount() {
        unreadCount.getAndUpdate {
            (it - 1).coerceAtLeast(0)
        }
    }
}
