package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultConnectivityProvider : ConnectivityProvider {
    private val status = MutableStateFlow(Connectivity.Status.Connected(false))

    override fun provide(): Connectivity {
        return Connectivity(provider = object : dev.jordond.connectivity.ConnectivityProvider {
            override fun monitor(): Flow<Connectivity.Status> = status
        })
    }
}
