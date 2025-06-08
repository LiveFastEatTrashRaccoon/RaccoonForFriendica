package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType

interface PushNotificationRepository {
    suspend fun create(
        endpoint: String,
        pubKey: String,
        auth: String,
        types: List<NotificationType>,
        policy: NotificationPolicy,
    ): String?

    suspend fun update(types: List<NotificationType>, policy: NotificationPolicy): String?

    suspend fun delete(): Boolean
}
