package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel

interface EventRepository {
    suspend fun getAll(pageCursor: String?): List<EventModel>?
}
