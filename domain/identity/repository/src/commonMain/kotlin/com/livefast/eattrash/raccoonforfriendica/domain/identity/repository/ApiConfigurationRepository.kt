package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ApiConfigurationRepository {
    val node: StateFlow<String>
    val isLogged: StateFlow<Boolean>

    fun changeNode(value: String)

    fun setAuth(credentials: Pair<String, String>? = null)
}
