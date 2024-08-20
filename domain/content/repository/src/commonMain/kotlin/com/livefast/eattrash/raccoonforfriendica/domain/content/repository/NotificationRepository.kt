package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

interface NotificationRepository {
    suspend fun getAll(
        types: List<NotificationType> = emptyList(),
        includeUnread: Boolean = false,
        pageCursor: String? = null,
    ): List<NotificationModel>

    suspend fun markAsRead(id: String): Boolean

    suspend fun markAllAsRead(): Boolean
}
