package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class DefaultNotificationRepository(
    private val provider: ServiceProvider,
) : NotificationRepository {
    private val mutex = Mutex()
    private val cachedValues: MutableList<NotificationModel> = mutableListOf()

    override suspend fun getAll(
        types: List<NotificationType>,
        pageCursor: String?,
        refresh: Boolean,
    ): List<NotificationModel>? =
        withContext(Dispatchers.IO) {
            if (refresh) {
                mutex.withLock {
                    cachedValues.clear()
                }
            }
            if (pageCursor == null && cachedValues.isNotEmpty()) {
                return@withContext cachedValues
            }
            runCatching {
                val response =
                    provider.notifications.get(
                        types = types.mapNotNull { it.toDto() },
                        maxId = pageCursor,
                        limit = DEFAULT_PAGE_SIZE,
                    )
                response
                    .map { it.toModel() }
                    .also {
                        if (pageCursor == null) {
                            mutex.withLock {
                                cachedValues.addAll(it)
                            }
                        }
                    }
            }.getOrNull()
        }

    override suspend fun dismiss(id: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val res = provider.notifications.dismiss(id)
                res.isSuccessful
            }.getOrElse { false }
        }

    override suspend fun dismissAll(): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val res = provider.notifications.clear()
                res.isSuccessful
            }.getOrElse { false }
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
