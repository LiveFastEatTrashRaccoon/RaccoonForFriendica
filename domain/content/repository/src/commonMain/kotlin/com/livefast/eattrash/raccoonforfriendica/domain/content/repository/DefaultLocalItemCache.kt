package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache

internal class DefaultLocalItemCache<T> : LocalItemCache<T> {
    private val cache = LruCache<String, T>(MAX_SIZE)

    override suspend fun put(
        key: String,
        value: T,
    ) {
        cache.put(key, value)
    }

    override suspend fun get(key: String): T? = cache.get(key)

    override suspend fun remove(key: String) {
        cache.remove(key)
    }

    override suspend fun clear() {
        cache.clear()
    }

    companion object {
        private const val MAX_SIZE = 20
    }
}
