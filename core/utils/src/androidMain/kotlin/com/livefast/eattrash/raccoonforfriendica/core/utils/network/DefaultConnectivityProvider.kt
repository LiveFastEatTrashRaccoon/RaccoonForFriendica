package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import org.koin.core.annotation.Single
import dev.jordond.connectivity.Connectivity

@Single
internal class DefaultConnectivityProvider : ConnectivityProvider {
    override fun provide(): Connectivity {
        return Connectivity()
    }
}
