package com.livefast.eattrash.raccoonforfriendica.core.utils.cache

interface LruCache<K, V> {
    suspend fun containsKey(key: K): Boolean

    suspend fun put(
        key: K,
        value: V,
    )

    suspend fun get(key: K): V?

    suspend fun remove(key: K)

    suspend fun clear()

    companion object {
        fun <K, V> factory(capacity: Int): LruCache<K, V> = DefaultLruCache(capacity)
    }
}
