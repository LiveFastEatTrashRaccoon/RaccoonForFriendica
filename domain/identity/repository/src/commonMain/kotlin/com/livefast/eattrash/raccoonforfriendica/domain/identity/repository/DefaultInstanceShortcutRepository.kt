package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore

internal class DefaultInstanceShortcutRepository(
    private val keyStore: TemporaryKeyStore,
) : InstanceShortcutRepository {
    override suspend fun getAll(accountId: Long): List<String> {
        val key = getKey(accountId)
        return keyStore.get(key = key, default = emptyList())
    }

    override suspend fun delete(
        accountId: Long,
        node: String,
    ): List<String> {
        val currentValues = getAll(accountId)
        val newValues = currentValues - node
        updateValues(accountId = accountId, values = newValues)
        return newValues
    }

    override suspend fun create(
        accountId: Long,
        node: String,
    ): List<String> {
        val currentValues = getAll(accountId)
        val newValues =
            if (currentValues.contains(node)) {
                currentValues
            } else {
                currentValues + node
            }
        updateValues(accountId = accountId, values = newValues)
        return newValues
    }

    private fun getKey(accountId: Long) = "InstanceShortcutRepository.$accountId.items"

    private fun updateValues(
        accountId: Long,
        values: List<String>,
    ) {
        val key = getKey(accountId)
        keyStore.save(key = key, values)
    }
}
