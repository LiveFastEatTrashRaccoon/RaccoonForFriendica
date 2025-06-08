package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

interface StopWordRepository {
    suspend fun get(accountId: Long?): List<String>

    suspend fun update(accountId: Long?, items: List<String>)
}
