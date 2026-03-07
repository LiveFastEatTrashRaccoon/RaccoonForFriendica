package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity

interface ConnectivityProvider {
    fun provide(): Connectivity
}
