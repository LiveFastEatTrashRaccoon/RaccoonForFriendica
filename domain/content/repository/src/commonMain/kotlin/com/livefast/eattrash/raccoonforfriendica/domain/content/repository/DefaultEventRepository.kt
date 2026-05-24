package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.utils.io.CancellationException

internal class DefaultEventRepository(private val provider: ServiceProvider) : EventRepository {
    override suspend fun getAll(pageCursor: String?): List<EventModel>? = try {
        provider.event
            .getAll(
                maxId = pageCursor?.toLong(),
                count = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
