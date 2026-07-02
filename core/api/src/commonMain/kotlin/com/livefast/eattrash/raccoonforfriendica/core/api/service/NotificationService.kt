package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType

interface NotificationService {
    suspend fun get(
        types: List<NotificationType>,
        excludeTypes: List<NotificationType>? = null,
        maxId: String? = null,
        minId: String? = null,
        includeAll: Boolean = false,
        limit: Int = 20,
    ): List<Notification>

    suspend fun dismiss(id: String): Boolean

    suspend fun clear(): Boolean
}
