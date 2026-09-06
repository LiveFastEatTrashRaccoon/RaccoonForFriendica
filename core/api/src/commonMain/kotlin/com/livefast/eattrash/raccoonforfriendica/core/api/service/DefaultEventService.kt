package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@AssistedInject
class DefaultEventService(@Assisted args: ServiceCreationArgs) : EventService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getAll(maxId: Long?, count: Int): List<Event> = client.get("$baseUrl/friendica/events") {
        parameter("since_id", maxId)
        parameter("count", count)
    }.body()
}

@AssistedFactory
fun interface EventServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultEventService
}
