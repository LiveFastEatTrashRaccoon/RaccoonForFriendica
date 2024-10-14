package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

interface NotificationRepository {
    suspend fun getAll(
        types: List<NotificationType> = NotificationType.ALL,
        pageCursor: String? = null,
        refresh: Boolean = false,
    ): List<NotificationModel>?

    suspend fun dismiss(id: String): Boolean

    suspend fun dismissAll(): Boolean
}
