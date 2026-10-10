package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.PagedList

interface NotificationRepository {
    suspend fun getAll(
        types: List<NotificationType> = NotificationType.ALL,
        pageCursor: String? = null,
        refresh: Boolean = false,
    ): PagedList<NotificationModel>?

    suspend fun dismiss(id: String): Boolean

    suspend fun dismissAll(): Boolean
}
