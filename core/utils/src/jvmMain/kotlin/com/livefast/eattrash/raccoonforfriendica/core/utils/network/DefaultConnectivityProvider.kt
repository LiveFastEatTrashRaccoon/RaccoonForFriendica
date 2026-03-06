package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class DefaultConnectivityProvider : ConnectivityProvider {
    private val _status = MutableStateFlow(Connectivity.Status.Connected(false))

    override fun provide(): Connectivity {
        return Connectivity(provider = object : dev.jordond.connectivity.ConnectivityProvider {
            override fun monitor(): Flow<Connectivity.Status> = _status
        })
    }
}
