package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FeedTypeModel
import kotlinx.coroutines.flow.Flow

interface FeedTypeRepository {

    suspend fun getAll(accountId: Long): List<FeedTypeModel>
    fun getAllAsFlow(accountId: Long): Flow<List<FeedTypeModel>>
    suspend fun getBy(accountId: Long, type: Int, info: String?): FeedTypeModel?

    suspend fun create(items: List<FeedTypeModel>)
    suspend fun create(item: FeedTypeModel)

    suspend fun update(item: FeedTypeModel)
    suspend fun update(items: List<FeedTypeModel>)

    suspend fun delete(item: FeedTypeModel)
}
