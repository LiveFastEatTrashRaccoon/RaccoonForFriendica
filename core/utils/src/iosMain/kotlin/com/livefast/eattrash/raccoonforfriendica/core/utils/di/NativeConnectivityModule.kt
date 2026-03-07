package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.network.ConnectivityProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.DefaultConnectivityProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal actual val nativeConnectivityModule =  DI.Module("NativeConnectivityModule") {
    bind<ConnectivityProvider> {
        singleton {
            DefaultConnectivityProvider()
        }
    }
}
