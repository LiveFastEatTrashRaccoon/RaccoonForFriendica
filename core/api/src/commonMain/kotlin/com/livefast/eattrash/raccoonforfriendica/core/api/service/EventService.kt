package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted

interface EventService {
    suspend fun getAll(maxId: Long? = null, count: Int): List<Event>
}

fun interface EventServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): EventService
}
