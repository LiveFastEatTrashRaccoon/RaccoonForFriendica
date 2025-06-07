package com.livefast.eattrash.raccoonforfriendica.core.utils.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DefaultLruCache<K, V>(private val capacity: Int) : LruCache<K, V> {
    private val mutex = Mutex()
    private val keysSortedByLastAccess = mutableListOf<K>()
    private val map = mutableMapOf<K, V>()

    override suspend fun containsKey(key: K) = mutex.withLock {
        keysSortedByLastAccess.contains(key)
    }

    override suspend fun put(key: K, value: V) = mutex.withLock {
        if (keysSortedByLastAccess.contains(key)) {
            keysSortedByLastAccess.remove(key)
        } else if (keysSortedByLastAccess.size >= capacity) {
            keysSortedByLastAccess.lastOrNull()?.also { lastKey ->
                keysSortedByLastAccess.remove(lastKey)
                map.remove(lastKey)
            }
        }
        map[key] = value
        keysSortedByLastAccess.add(0, key)
    }

    override suspend fun get(key: K): V? = mutex.withLock {
        if (keysSortedByLastAccess.contains(key)) {
            keysSortedByLastAccess.remove(key)
            keysSortedByLastAccess.add(0, key)
        }
        return map[key]
    }

    override suspend fun remove(key: K): Unit = mutex.withLock {
        map.remove(key)
    }

    override suspend fun clear() = mutex.withLock {
        map.clear()
    }
}
