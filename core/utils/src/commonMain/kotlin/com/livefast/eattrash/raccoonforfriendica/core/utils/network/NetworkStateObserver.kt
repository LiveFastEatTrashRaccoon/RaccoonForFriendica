package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import kotlinx.coroutines.flow.StateFlow

sealed interface NetworkState {
    data class Connected(val metered: Boolean) : NetworkState

    data object Disconnected : NetworkState
}

interface NetworkStateObserver {
    val state: StateFlow<NetworkState>

    fun start()

    fun stop()
}
