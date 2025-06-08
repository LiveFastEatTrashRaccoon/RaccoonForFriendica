package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

interface AccountCredentialsCache {
    fun get(accountId: Long): ApiCredentials?

    fun save(accountId: Long, credentials: ApiCredentials)

    fun remove(accountId: Long)
}
