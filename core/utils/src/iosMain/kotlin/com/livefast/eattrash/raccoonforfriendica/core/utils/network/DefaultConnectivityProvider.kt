package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity
import org.koin.core.annotation.Single

@Single
internal class DefaultConnectivityProvider : ConnectivityProvider {
    override fun provide(): Connectivity {
        return Connectivity()
    }
}
