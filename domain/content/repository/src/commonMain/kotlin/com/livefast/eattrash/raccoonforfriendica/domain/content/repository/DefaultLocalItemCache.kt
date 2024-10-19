package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache

internal class DefaultLocalItemCache<T> : LocalItemCache<T> {
    private val cache = LruCache<String, T>(MAX_SIZE)

    override fun put(
        key: String,
        value: T,
    ) {
        cache.put(key, value)
    }

    override fun get(key: String): T? = cache.get(key)

    override fun remove(key: String) {
        cache.remove(key)
    }

    override fun clear() {
        cache.clear()
    }

    companion object {
        private const val MAX_SIZE = 20
    }
}
