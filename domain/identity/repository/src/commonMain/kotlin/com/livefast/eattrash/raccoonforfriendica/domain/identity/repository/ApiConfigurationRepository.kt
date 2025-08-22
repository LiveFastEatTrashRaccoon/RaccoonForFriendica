package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ApiConfigurationRepository {
    val node: StateFlow<String>
    val isLogged: StateFlow<Boolean>

    suspend fun getDefaultNode(): String

    suspend fun changeNode(value: String)

    suspend fun setAuth(credentials: ApiCredentials? = null)

    suspend fun hasCachedAuthCredentials(): Boolean

    suspend fun refresh(): Result<Unit>
}
