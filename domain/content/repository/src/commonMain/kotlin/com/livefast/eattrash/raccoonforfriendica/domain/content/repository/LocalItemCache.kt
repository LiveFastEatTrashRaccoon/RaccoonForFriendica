package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

interface LocalItemCache<T> {
    suspend fun put(
        key: String,
        value: T,
    )

    suspend fun get(key: String): T?

    suspend fun remove(key: String)

    suspend fun clear()
}
