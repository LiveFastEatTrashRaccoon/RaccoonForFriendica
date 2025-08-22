package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import kotlinx.coroutines.flow.SharedFlow

interface AuthManager {
    val credentialFlow: SharedFlow<ApiCredentials>

    fun openLogin(type: LoginType)

    fun openLegacyLogin()

    suspend fun startOAuthFlow(node: String): String

    suspend fun performTokenExchange(url: String)

    suspend fun performRefresh(refreshToken: String): ApiCredentials?

    fun openNewAccount()
}
