package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

object IosAuthHelper {
    var authManager: AuthManager? = null

    suspend fun performTokenExchange(url: String) {
        authManager?.performTokenExchange(url)
    }
}
