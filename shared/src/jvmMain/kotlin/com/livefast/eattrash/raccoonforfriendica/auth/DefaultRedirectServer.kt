package com.livefast.eattrash.raccoonforfriendica.auth

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultRedirectServer(
    private val server: EmbeddedRedirectServer,
) : RedirectServer {

    override val isLocalServerRequired = true

    override fun start(): Int = server.start()

    override suspend fun waitForCode(): String = server.waitForCode()

    override fun stop() = server.stop()
}
