package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

interface NotificationRepository {
    suspend fun getAll(
        types: List<NotificationType> = emptyList(),
        pageCursor: String? = null,
    ): List<NotificationModel>
}
