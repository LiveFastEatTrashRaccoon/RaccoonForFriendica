package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import io.ktor.client.HttpClient

interface EventService {
    suspend fun getAll(maxId: Long? = null, count: Int): List<Event>
}

fun interface EventServiceFactory {
    fun create(baseUrl: String, client: HttpClient): EventService
}
