package com.livefast.eattrash.raccoonforfriendica.auth

internal expect class DefaultRedirectServer : RedirectServer {
    override val isLocalServerRequired: Boolean

    override fun start(): Int

    override suspend fun waitForCode(): String

    override fun stop()
}
