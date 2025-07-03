package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class DefaultEventService(private val baseUrl: String, private val client: HttpClient) : EventService {
    override suspend fun getAll(maxId: Long?, count: Int): List<Event> = client.get("$baseUrl/friendica/events") {
        parameter("since_id", maxId)
        parameter("count", count)
    }.body()
}
