package com.livefast.eattrash.raccoonforfriendica.core.translation.store

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.utils.uuid.getUuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json

internal class DefaultTranslationProviderConfigStore(private val keyStore: TemporaryKeyStore) :
    TranslationProviderConfigStore {
    private val valuesFlow = MutableSharedFlow<List<TranslationProviderConfig>>(replay = 1)

    override suspend fun getAll(): List<TranslationProviderConfig> = getStoredIds()
        .mapNotNull { id ->
            getById(id)
        }

    override suspend fun observe(): Flow<List<TranslationProviderConfig>> = valuesFlow.asSharedFlow().onStart {
        emitUpdate()
    }

    override suspend fun getById(id: String): TranslationProviderConfig? {
        val defaultId = getDefaultId()
        val key = getKey(id)
        val rawValue = keyStore.get(key, "{}")
        return runCatching {
            JsonSerializer.decodeFromString<TranslationProviderConfig>(rawValue)
        }.getOrNull()?.let {
            it.copy(default = it.id == defaultId)
        }
    }

    override suspend fun getDefaultId(): String? {
        val key = getKey(KEY_DEFAULT_ID)
        return keyStore.get(key, "").takeIf { it.isNotEmpty() }
    }

    override suspend fun setDefaultId(id: String) {
        val key = getKey(KEY_DEFAULT_ID)
        keyStore.save(key, id)
        emitUpdate()
    }

    override suspend fun create(config: TranslationProviderConfig): String {
        val id = generateId()
        update(id, config.copy(id = id))
        val storedIds = getStoredIds()
        keyStore.save(getKey(KEY_INDEX), storedIds + id)
        emitUpdate()
        return id
    }

    override suspend fun update(id: String, config: TranslationProviderConfig) {
        val key = getKey(id)
        val rawValue = JsonSerializer.encodeToString(config)
        keyStore.save(key, rawValue)
        emitUpdate()
    }

    override suspend fun delete(id: String) {
        val key = getKey(id)
        keyStore.remove(key)
        val storedIds = getStoredIds()
        keyStore.save(getKey(KEY_INDEX), storedIds - id)
        emitUpdate()
    }

    private fun getKey(key: String): String = "$KEY_PREFIX.$key"

    private fun generateId(): String = getUuid()

    private suspend fun getStoredIds(): List<String> {
        val indexKey = getKey(KEY_INDEX)
        return keyStore.get(indexKey, emptyList())
    }

    private suspend fun emitUpdate() {
        val values = getAll()
        valuesFlow.emit(values)
    }

    companion object {
        private const val KEY_PREFIX = "translation_provider_config"
        private const val KEY_INDEX = "index"
        private const val KEY_DEFAULT_ID = "default_id"
        private val JsonSerializer = Json { ignoreUnknownKeys = true }
    }
}
