package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.network.ConnectivityProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.DefaultConnectivityProvider
import org.koin.dsl.module


internal actual val nativeConnectivityModule = module {
    single<ConnectivityProvider> {
        DefaultConnectivityProvider()
    }
}
