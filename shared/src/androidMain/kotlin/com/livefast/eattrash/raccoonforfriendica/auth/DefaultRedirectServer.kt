package com.livefast.eattrash.raccoonforfriendica.auth

import org.koin.core.annotation.Single

@Single

internal actual class DefaultRedirectServer : RedirectServer {

    actual override val isLocalServerRequired = false

    actual override fun start() = 0

    actual override suspend fun waitForCode() = ""

    actual override fun stop() = Unit
}
