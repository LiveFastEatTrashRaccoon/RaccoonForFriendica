package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor

interface TimelineRepository {
    suspend fun getPublic(pageCursor: String? = null, refresh: Boolean = false): List<TimelineEntryModel>?

    suspend fun getHome(pageCursor: String? = null, refresh: Boolean = false): List<TimelineEntryModel>?

    suspend fun getLocal(
        pageCursor: String? = null,
        refresh: Boolean = false,
        otherInstance: String? = null,
    ): List<TimelineEntryModel>?

    suspend fun getHashtag(hashtag: String, pageCursor: String? = null): ListWithPageCursor<TimelineEntryModel>?

    suspend fun getCircle(id: String, pageCursor: String? = null): List<TimelineEntryModel>?
}
