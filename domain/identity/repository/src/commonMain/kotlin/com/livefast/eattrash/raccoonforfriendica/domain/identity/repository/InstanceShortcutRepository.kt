package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

interface InstanceShortcutRepository {
    suspend fun getAll(accountId: Long): List<String>

    suspend fun delete(
        accountId: Long,
        node: String,
    ): List<String>

    suspend fun create(
        accountId: Long,
        node: String,
    ): List<String>
}
