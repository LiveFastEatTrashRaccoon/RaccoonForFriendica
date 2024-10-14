package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.hasLaterIdThan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

internal class DefaultInboxManager(
    private val notificationRepository: NotificationRepository,
    private val markerRepository: MarkerRepository,
) : InboxManager {
    override val unreadCount = MutableStateFlow(0)

    override suspend fun refreshUnreadCount() {
        val lastReadId = markerRepository.get(MarkerType.Notifications)?.lastReadId
        val notifications = notificationRepository.getAll()
        unreadCount.update {
            notifications?.count { it.hasLaterIdThan(lastReadId) } ?: 0
        }
    }

    override suspend fun decrementUnreadCount() {
        unreadCount.getAndUpdate {
            (it - 1).coerceAtLeast(0)
        }
    }
}
