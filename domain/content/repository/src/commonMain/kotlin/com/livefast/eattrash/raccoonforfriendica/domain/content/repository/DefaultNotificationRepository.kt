package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultNotificationRepository(
    private val provider: ServiceProvider,
) : NotificationRepository {
    override suspend fun getAll(
        types: List<NotificationType>,
        includeAll: Boolean,
        pageCursor: String?,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val response =
                provider.notifications.get(
                    types = types.mapNotNull { it.toDto() },
                    maxId = pageCursor,
                    includeAll = includeAll,
                    limit = DEFAULT_PAGE_SIZE,
                )
            response.map { it.toModel() }
        }
    }.getOrElse { emptyList() }

    override suspend fun markAsRead(id: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.notifications.dismiss(id)
                true
            }.getOrElse { false }
        }

    override suspend fun markAllAsRead(): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.notifications.dismissAll()
                true
            }.getOrElse { false }
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
