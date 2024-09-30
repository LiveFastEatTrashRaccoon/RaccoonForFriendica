package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

internal class DefaultInboxManager(
    private val notificationRepository: NotificationRepository,
) : InboxManager {
    override val unreadCount = MutableStateFlow(0)

    override suspend fun refreshUnreadCount() {
        val notifications = notificationRepository.getAll()
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
