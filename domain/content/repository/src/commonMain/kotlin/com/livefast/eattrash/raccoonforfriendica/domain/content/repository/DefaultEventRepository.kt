package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel

internal class DefaultEventRepository(private val provider: ServiceProvider) : EventRepository {
    override suspend fun getAll(pageCursor: String?): List<EventModel>? = runCatching {
        provider.events
            .getAll(
                maxId = pageCursor?.toLong(),
                count = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
