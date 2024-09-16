package com.livefast.eattrash.raccoonforfriendica.core.utils.cache

class LruCache<K, V>(
    private val capacity: Int,
) {
    private val keysSortedByLastAccess = mutableListOf<K>()
    private val map = mutableMapOf<K, V>()

    fun containsKey(key: K) = keysSortedByLastAccess.contains(key)

    fun put(
        key: K,
        value: V,
    ) {
        if (keysSortedByLastAccess.contains(key)) {
            keysSortedByLastAccess.remove(key)
        } else if (keysSortedByLastAccess.size > capacity) {
            keysSortedByLastAccess.lastOrNull()?.also { lastKey ->
                keysSortedByLastAccess.remove(lastKey)
                map.remove(lastKey)
            }
        }
        map[key] = value
        keysSortedByLastAccess.add(0, key)
    }

    fun get(key: K): V? {
        if (keysSortedByLastAccess.contains(key)) {
            keysSortedByLastAccess.remove(key)
            keysSortedByLastAccess.add(0, key)
        }
        return map[key]
    }

    fun remove(key: K) {
        map.remove(key)
    }

    fun clear() {
        map.clear()
    }
}
