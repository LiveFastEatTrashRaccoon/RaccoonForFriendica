package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineContextModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility

interface TimelineEntryRepository {
    suspend fun getByUser(
        userId: String,
        pageCursor: String? = null,
        excludeReplies: Boolean = false,
        excludeReblogs: Boolean = false,
        pinned: Boolean = false,
        onlyMedia: Boolean = false,
    ): List<TimelineEntryModel>

    suspend fun getById(id: String): TimelineEntryModel?

    suspend fun getContext(id: String): TimelineContextModel?

    suspend fun reblog(id: String): TimelineEntryModel?

    suspend fun unreblog(id: String): TimelineEntryModel?

    suspend fun pin(id: String): TimelineEntryModel?

    suspend fun unpin(id: String): TimelineEntryModel?

    suspend fun favorite(id: String): TimelineEntryModel?

    suspend fun unfavorite(id: String): TimelineEntryModel?

    suspend fun bookmark(id: String): TimelineEntryModel?

    suspend fun unbookmark(id: String): TimelineEntryModel?

    suspend fun getFavorites(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getBookmarks(pageCursor: String? = null): List<TimelineEntryModel>

    suspend fun getUsersWhoFavorited(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>

    suspend fun getUsersWhoReblogged(
        id: String,
        pageCursor: String? = null,
    ): List<UserModel>

    suspend fun create(
        localId: String,
        text: String,
        spoilerText: String? = null,
        inReplyTo: String? = null,
        sensitive: Boolean = false,
        mediaIds: List<String>? = null,
        visibility: Visibility,
        lang: String? = null,
    ): TimelineEntryModel?

    suspend fun delete(id: String): Boolean
}
