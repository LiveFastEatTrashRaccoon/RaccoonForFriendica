package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
internal class DefaultNotificationRepository(private val provider: ServiceProvider) : NotificationRepository {
    private val mutex = Mutex()
    private val cachedValues: MutableList<NotificationModel> = mutableListOf()

    override suspend fun getAll(
        types: List<NotificationType>,
        pageCursor: String?,
        refresh: Boolean,
    ): ListWithPageCursor<NotificationModel>? {
        if (refresh) {
            mutex.withLock {
                cachedValues.clear()
            }
        }
        if (pageCursor == null && cachedValues.isNotEmpty()) {
            return ListWithPageCursor(list = cachedValues, cursor = null) // Simplified for cache
        }
        return try {
            val (list, cursor) =
                provider.notification.get(
                    types = types.mapNotNull { it.toDto() },
                    maxId = pageCursor,
                    limit = DEFAULT_PAGE_SIZE,
                )
            val models = list.map { it.toModel() }
            if (pageCursor == null) {
                mutex.withLock {
                    cachedValues.addAll(models)
                }
            }
            ListWithPageCursor(list = models, cursor = cursor)
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
