package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import kotlinx.coroutines.flow.StateFlow

interface ApiConfigurationRepository {
    val node: StateFlow<String>
    val isLogged: StateFlow<Boolean>

    suspend fun getDefaultNode(): String

    suspend fun changeNode(value: String)

    suspend fun setAuth(credentials: ApiCredentials? = null)

    suspend fun hasCachedAuthCredentials(): Boolean

    suspend fun refresh(): Result<Unit>
}
