package com.livefast.eattrash.raccoonforfriendica.auth


internal class DefaultRedirectServer : RedirectServer {

    override val isLocalServerRequired = false

    override fun start() = 0

    override suspend fun waitForCode() = ""

    override fun stop() = Unit
}
