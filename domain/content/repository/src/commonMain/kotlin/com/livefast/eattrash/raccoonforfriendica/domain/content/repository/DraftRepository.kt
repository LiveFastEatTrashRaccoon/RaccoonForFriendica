package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface DraftRepository {
    suspend fun getAll(page: Int = 0): List<TimelineEntryModel>?

    suspend fun getById(id: String): TimelineEntryModel?

    suspend fun create(item: TimelineEntryModel): TimelineEntryModel?

    suspend fun update(item: TimelineEntryModel): TimelineEntryModel?

    suspend fun delete(id: String): Boolean
}
