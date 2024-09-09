package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

internal class DefaultLocalItemCache<T> : LocalItemCache<T> {
    private val keysSortedByLastAccess = mutableListOf<String>()
    private val map = mutableMapOf<String, T>()

    override fun put(
        key: String,
        value: T,
    ) {
        if (keysSortedByLastAccess.contains(key)) {
            keysSortedByLastAccess.remove(key)
        } else if (keysSortedByLastAccess.size > MAX_SIZE) {
            keysSortedByLastAccess.lastOrNull()?.also { lastKey ->
                keysSortedByLastAccess.remove(lastKey)
                map.remove(lastKey)
            }
        }
        map[key] = value
        keysSortedByLastAccess.add(0, key)
    }

    override fun get(key: String): T? {
        if (keysSortedByLastAccess.contains(key)) {
            keysSortedByLastAccess.remove(key)
            keysSortedByLastAccess.add(0, key)
        }
        return map[key]
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun clear() {
        map.clear()
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}
