package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

interface LocalItemCache<T> {
    fun put(
        key: String,
        value: T,
    )

    fun get(key: String): T?

    fun remove(key: String)

    fun clear()
}
