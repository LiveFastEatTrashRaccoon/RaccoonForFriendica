package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toRawValue
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultNotificationRepository(private val provider: ServiceProvider) : NotificationRepository {
    private val mutex = Mutex()
    private val cachedValues: MutableList<NotificationModel> = mutableListOf()

    override suspend fun getAll(
        types: List<NotificationType>,
        pageCursor: String?,
        refresh: Boolean,
    ): List<NotificationModel>? {
        if (refresh) {
            mutex.withLock {
                cachedValues.clear()
            }
        }
        if (pageCursor == null && cachedValues.isNotEmpty()) {
            return cachedValues
        }
        return runCatching {
            val response =
                provider.notifications.get(
                    types = types.mapNotNull { it.toRawValue() },
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

    override suspend fun dismiss(id: String): Boolean = provider.notifications.dismiss(id)

    override suspend fun dismissAll(): Boolean = provider.notifications.clear()

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
