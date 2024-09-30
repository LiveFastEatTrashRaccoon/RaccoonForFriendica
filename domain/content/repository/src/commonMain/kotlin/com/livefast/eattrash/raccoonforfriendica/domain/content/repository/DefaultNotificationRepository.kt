package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultNotificationRepository(
    private val provider: ServiceProvider,
) : NotificationRepository {
    private val cachedValues: MutableList<NotificationModel> = mutableListOf()

    override suspend fun getAll(
        types: List<NotificationType>,
        includeAll: Boolean,
        pageCursor: String?,
        refresh: Boolean,
    ): List<NotificationModel>? =
        withContext(Dispatchers.IO) {
            if (refresh) {
                cachedValues.clear()
            }
            if (pageCursor == null && cachedValues.isNotEmpty()) {
                return@withContext cachedValues
            }
            runCatching {
                val response =
                    provider.notifications.get(
                        types = types.mapNotNull { it.toDto() },
                        maxId = pageCursor,
                        includeAll = includeAll,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response
                    .map { it.toModel() }
                    .also {
                        if (pageCursor == null) {
                            cachedValues.addAll(it)
                        }
                    }
            }.getOrNull()
        }

    override suspend fun markAsRead(id: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val res = provider.notifications.dismiss(id)
                res.isSuccessful
            }.getOrElse { false }
        }

    override suspend fun markAllAsRead(): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val res = provider.notifications.dismissAll()
                res.isSuccessful
            }.getOrElse { false }
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
