package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity

internal class DefaultConnectivityProvider : ConnectivityProvider {
    override fun provide(): Connectivity {
        return Connectivity()
    }
}
