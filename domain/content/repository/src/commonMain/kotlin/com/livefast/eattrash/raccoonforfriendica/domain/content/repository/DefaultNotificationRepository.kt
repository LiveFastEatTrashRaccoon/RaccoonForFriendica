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
        pageCursor: String?,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val response =
                provider.notifications.get(
                    types = types.mapNotNull { it.toDto() },
                    maxId = pageCursor,
                    limit = DEFAULT_PAGE_SIZE,
                )
            response.map { it.toModel() }
        }
    }.getOrElse { emptyList() }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
