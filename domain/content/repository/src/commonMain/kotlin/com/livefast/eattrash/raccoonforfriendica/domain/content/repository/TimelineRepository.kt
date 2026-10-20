package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.PagedList

interface TimelineRepository {
    suspend fun getPublic(pageCursor: String? = null, refresh: Boolean = false): List<PostModel>?

    suspend fun getHome(pageCursor: String? = null, refresh: Boolean = false): List<PostModel>?

    suspend fun getLocal(
        pageCursor: String? = null,
        refresh: Boolean = false,
        otherInstance: String? = null,
    ): List<PostModel>?

    suspend fun getHashtag(
        hashtag: String,
        pageCursor: String? = null,
        otherInstance: String? = null,
    ): PagedList<PostModel>?

    suspend fun getCircle(id: String, pageCursor: String? = null): List<PostModel>?
}
