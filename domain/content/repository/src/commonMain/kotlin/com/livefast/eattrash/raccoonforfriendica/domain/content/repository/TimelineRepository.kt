package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

interface TimelineRepository {
    suspend fun getPublic(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getHome(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getLocal(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getHashtag(
        hashtag: String,
        pageCursor: String? = null,
    ): List<TimelineEntryModel>

    suspend fun getByAccount(
        accountId: String,
        pageCursor: String? = null,
        excludeReplies: Boolean = false,
        excludeReblogs: Boolean = false,
        pinned: Boolean = false,
        onlyMedia: Boolean = false,
    ): List<TimelineEntryModel>

    suspend fun getById(entryId: String): TimelineEntryModel?

    suspend fun getContext(entryId: String): TimelineContextModel?
}
