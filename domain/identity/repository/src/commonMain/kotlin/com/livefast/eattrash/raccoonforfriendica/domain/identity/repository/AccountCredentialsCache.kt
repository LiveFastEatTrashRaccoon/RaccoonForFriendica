package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

interface AccountCredentialsCache {
    suspend fun get(accountId: Long): ApiCredentials?

    suspend fun save(accountId: Long, credentials: ApiCredentials)

    suspend fun remove(accountId: Long)
}
