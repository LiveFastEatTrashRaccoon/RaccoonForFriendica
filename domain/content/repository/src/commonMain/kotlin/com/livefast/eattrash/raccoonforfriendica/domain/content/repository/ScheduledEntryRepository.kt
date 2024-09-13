package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface ScheduledEntryRepository {
    suspend fun getAll(pageCursor: String?): List<TimelineEntryModel>?

    suspend fun getById(id: String): TimelineEntryModel?

    suspend fun update(
        id: String,
        date: String,
    ): TimelineEntryModel?

    suspend fun delete(id: String): Boolean
}
