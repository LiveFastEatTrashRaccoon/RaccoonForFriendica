package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface ScheduledPostRepository {
    suspend fun getAll(pageCursor: String?): List<PostModel>?

    suspend fun getById(id: String): PostModel?

    suspend fun update(id: String, date: String): PostModel?

    suspend fun delete(id: String): Boolean
}
