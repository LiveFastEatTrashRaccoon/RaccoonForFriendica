package com.livefast.eattrash.raccoonforfriendica.auth

interface RedirectServer {
    val isLocalServerRequired: Boolean
    fun start(): Int
    suspend fun waitForCode(): String
    fun stop()
}
