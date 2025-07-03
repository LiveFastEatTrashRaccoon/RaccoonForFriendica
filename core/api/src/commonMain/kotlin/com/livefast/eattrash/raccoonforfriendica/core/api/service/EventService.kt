package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event

interface EventService {
    suspend fun getAll(maxId: Long? = null, count: Int): List<Event>
}
