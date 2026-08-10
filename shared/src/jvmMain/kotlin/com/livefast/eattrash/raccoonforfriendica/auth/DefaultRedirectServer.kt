package com.livefast.eattrash.raccoonforfriendica.auth

import org.koin.core.annotation.Single

@Single
internal actual class DefaultRedirectServer(
    private val server: EmbeddedRedirectServer,
) : RedirectServer {

    actual override val isLocalServerRequired = true

    actual override fun start(): Int = server.start()

    actual override suspend fun waitForCode(): String = server.waitForCode()

    actual override fun stop() = server.stop()
}
