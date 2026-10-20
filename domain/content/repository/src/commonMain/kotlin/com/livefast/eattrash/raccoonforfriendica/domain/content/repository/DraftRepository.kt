package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel

interface DraftRepository {
    suspend fun getAll(page: Int = 0): List<PostModel>?

    suspend fun getById(id: String): PostModel?

    suspend fun create(item: PostModel): PostModel?

    suspend fun update(item: PostModel): PostModel?

    suspend fun delete(id: String): Boolean
}
