package com.livefast.eattrash.raccoonforfriendica.core.preferences.store

import com.livefast.eattrash.raccoonforfriendica.core.preferences.settings.SettingsWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultTemporaryKeyStore(
    private val settings: SettingsWrapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TemporaryKeyStore {
    override suspend fun containsKey(key: String): Boolean = withContext(dispatcher) {
        settings.keys.contains(key)
    }

    override suspend fun save(key: String, value: Boolean) {
        withContext(dispatcher) {
            settings[key] = value
        }
    }

    override suspend fun get(key: String, default: Boolean): Boolean = withContext(dispatcher) {
        settings[key, default]
    }

    override suspend fun save(key: String, value: String) {
        withContext(dispatcher) {
            settings[key] = value
        }
    }

    override suspend fun get(key: String, default: String): String = withContext(dispatcher) {
        settings[key, default]
    }

    override suspend fun save(key: String, value: Int) {
        withContext(dispatcher) {
            settings[key] = value
        }
    }

    override suspend fun get(key: String, default: Int): Int = withContext(dispatcher) {
        settings[key, default]
    }

    override suspend fun save(key: String, value: Float) {
        withContext(dispatcher) {
            settings[key] = value
        }
    }

    override suspend fun get(key: String, default: Float): Float = withContext(dispatcher) {
        settings[key, default]
    }

    override suspend fun save(key: String, value: Double) {
        withContext(dispatcher) {
            settings[key] = value
        }
    }

    override suspend fun get(key: String, default: Double): Double = withContext(dispatcher) {
        settings[key, default]
    }

    override suspend fun save(key: String, value: Long) {
        withContext(dispatcher) {
            settings[key] = value
        }
    }

    override suspend fun get(key: String, default: Long): Long = withContext(dispatcher) {
        settings[key, default]
    }

    override suspend fun get(key: String, default: List<String>, delimiter: String): List<String> =
        withContext(dispatcher) {
            check(settings.hasKey(key)) { return@withContext default }
            val joined = settings[key, ""]
            joined.split(delimiter)
        }

    override suspend fun save(key: String, value: List<String>, delimiter: String) {
        withContext(dispatcher) {
            settings[key] = value.joinToString(delimiter)
        }
    }

    override suspend fun remove(key: String) {
        withContext(dispatcher) {
            settings.remove(key)
        }
    }

    override suspend fun removeAll() {
        withContext(dispatcher) {
            settings.clear()
        }
    }
}
