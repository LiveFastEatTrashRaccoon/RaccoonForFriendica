package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import kotlinx.coroutines.flow.SharedFlow

interface AuthManager {
    val credentialFlow: SharedFlow<ApiCredentials>

    fun openLogin()

    fun openLegacyLogin()

    suspend fun startOAuthFlow(node: String): String

    suspend fun performTokenExchange(url: String)
}
