package com.livefast.eattrash.raccoonforfriendica.core.translation.store

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing the persistence of translation provider configurations.
 */
interface TranslationProviderConfigStore {
    /**
     * Retrieves all stored translation provider configurations.
     *
     * @return a list of all [TranslationProviderConfig] objects
     */
    suspend fun getAll(): List<TranslationProviderConfig>

    /**
     * Observes changes to the list of translation provider configurations.
     *
     * @return a [Flow] emitting a list of all [TranslationProviderConfig] objects
     */
    suspend fun observe(): Flow<List<TranslationProviderConfig>>

    /**
     * Retrieves a specific translation provider configuration by its unique identifier.
     *
     * @param id unique identifier of the configuration to retrieve
     * @return a [TranslationProviderConfig] if found, or null if no configuration exists with the given [id]
     */
    suspend fun getById(id: String): TranslationProviderConfig?

    /**
     * Retrieves the identifier of the translation provider configuration currently set as default.
     *
     * @return the unique identifier of the default translation provider, or null if no default is set
     */
    suspend fun getDefaultId(): String?

    /**
     * Sets a specific translation provider configuration as the default one.
     *
     * @param id unique identifier of the translation provider to set as default
     */
    suspend fun setDefaultId(id: String)

    /**
     * Persists a new translation provider configuration.
     *
     * @param config [TranslationProviderConfig] to create
     * @return the unique identifier assigned to the newly created configuration
     */
    suspend fun create(config: TranslationProviderConfig): String

    /**
     * Updates an existing translation provider configuration.
     *
     * @param id unique identifier of the configuration to update
     * @param config new [TranslationProviderConfig] data
     */
    suspend fun update(id: String, config: TranslationProviderConfig)

    /**
     * Deletes a translation provider configuration from the store.
     *
     * @param id unique identifier of the configuration to delete
     */
    suspend fun delete(id: String)
}
