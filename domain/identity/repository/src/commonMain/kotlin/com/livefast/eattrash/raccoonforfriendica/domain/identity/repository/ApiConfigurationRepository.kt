package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ApiConfigurationRepository {
    val node: StateFlow<String>
    val isLogged: StateFlow<Boolean>
    val defaultNode: String

    suspend fun initialize()

    fun changeNode(value: String)

    fun setAuth(credentials: ApiCredentials? = null)

    suspend fun hasCachedAuthCredentials(): Boolean
}
