package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class DefaultEventRepository(
    @Named("default") private val provider: ServiceProvider,
) : EventRepository {
    override suspend fun getAll(pageCursor: String?): List<EventModel>? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.events
                    .getAll(
                        maxId = pageCursor?.toLong(),
                        count = DEFAULT_PAGE_SIZE,
                    ).map { it.toModel() }
            }.getOrNull()
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
