package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@AssistedInject
class DefaultEventService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    EventService {

    override suspend fun getAll(maxId: Long?, count: Int): List<Event> = client.get("$baseUrl/friendica/events") {
        parameter("since_id", maxId)
        parameter("count", count)
    }.body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultEventServiceFactory : EventServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultEventService
}
