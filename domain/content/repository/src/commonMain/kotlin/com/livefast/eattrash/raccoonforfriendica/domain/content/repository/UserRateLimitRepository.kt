package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel

interface UserRateLimitRepository {
    suspend fun getAll(accountId: Long): List<UserRateLimitModel>

    suspend fun getBy(
        handle: String,
        accountId: Long,
    ): UserRateLimitModel?

    suspend fun create(model: UserRateLimitModel): UserRateLimitModel?

    suspend fun update(model: UserRateLimitModel): UserRateLimitModel?

    suspend fun delete(id: Long): Boolean
}
