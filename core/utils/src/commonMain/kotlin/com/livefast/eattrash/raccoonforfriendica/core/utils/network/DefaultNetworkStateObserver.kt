package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single

@Single
internal class DefaultNetworkStateObserver(
    private val connectivity: Connectivity = Connectivity(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NetworkStateObserver {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override val state =
        connectivity.statusUpdates
            .map { connectivity ->
                when (connectivity) {
                    is Connectivity.Status.Connected -> NetworkState.Connected(metered = connectivity.metered)
                    Connectivity.Status.Disconnected -> NetworkState.Disconnected
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = NetworkState.Disconnected,
            )

    override fun start() {
        connectivity.start()
    }

    override fun stop() {
        connectivity.stop()
    }
}
