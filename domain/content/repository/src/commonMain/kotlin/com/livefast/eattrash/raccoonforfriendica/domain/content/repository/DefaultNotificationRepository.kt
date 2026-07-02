package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.utils.io.CancellationException
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
        return try {
            val response =
                provider.notification.get(
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
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }
    }

    override suspend fun dismiss(id: String): Boolean = provider.notification.dismiss(id)

    override suspend fun dismissAll(): Boolean = provider.notification.clear()

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
