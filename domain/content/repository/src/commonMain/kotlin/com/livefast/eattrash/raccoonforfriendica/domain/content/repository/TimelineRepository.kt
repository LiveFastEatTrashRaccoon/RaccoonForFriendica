package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TimelineRepository {
    suspend fun getPublic(pageCursor: String? = null): List<TimelineEntryModel>?

    suspend fun getHome(pageCursor: String? = null): List<TimelineEntryModel>?

    suspend fun getLocal(pageCursor: String? = null): List<TimelineEntryModel>?

    suspend fun getHashtag(
        hashtag: String,
        pageCursor: String? = null,
    ): List<TimelineEntryModel>?

    suspend fun getCircle(
        id: String,
        pageCursor: String? = null,
    ): List<TimelineEntryModel>?
}
