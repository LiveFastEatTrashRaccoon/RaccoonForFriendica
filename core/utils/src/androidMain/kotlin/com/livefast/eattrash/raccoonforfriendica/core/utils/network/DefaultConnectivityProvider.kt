package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultConnectivityProvider : ConnectivityProvider {
    override fun provide(): Connectivity = Connectivity()
}
