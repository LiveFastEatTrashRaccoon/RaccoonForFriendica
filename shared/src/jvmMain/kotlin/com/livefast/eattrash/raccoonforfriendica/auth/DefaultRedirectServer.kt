package com.livefast.eattrash.raccoonforfriendica.auth


internal class DefaultRedirectServer(
    private val server: EmbeddedRedirectServer,
) : RedirectServer {

    override val isLocalServerRequired = true

    override fun start(): Int = server.start()

    override suspend fun waitForCode(): String = server.waitForCode()

    override fun stop() = server.stop()
}
