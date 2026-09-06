package com.livefast.eattrash.raccoonforfriendica.auth

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultRedirectServer : RedirectServer {

    override val isLocalServerRequired = false

    override fun start() = 0

    override suspend fun waitForCode() = ""

    override fun stop() = Unit
}
