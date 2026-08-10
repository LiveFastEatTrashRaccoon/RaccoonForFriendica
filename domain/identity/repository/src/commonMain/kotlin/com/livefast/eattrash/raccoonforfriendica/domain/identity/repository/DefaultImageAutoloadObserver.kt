package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkState
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ImageLoadingMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single

@Single
internal class DefaultImageAutoloadObserver(
    networkStateObserver: NetworkStateObserver,
    settingsRepository: SettingsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ImageAutoloadObserver {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override val enabled =
        combine(
            settingsRepository.current,
            networkStateObserver.state,
        ) { settings, networkState ->
            when (settings?.autoloadImages) {
                ImageLoadingMode.OnWifi -> networkState is NetworkState.Connected && !networkState.metered
                ImageLoadingMode.OnDemand -> false
                else -> true
            }
        }.stateIn(
            scope,
            SharingStarted.Lazily,
            true,
        )
}
