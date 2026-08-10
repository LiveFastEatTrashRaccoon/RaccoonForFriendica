package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.network.ConnectivityProvider
import dev.jordond.connectivity.Connectivity
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [NativeUtilsModule::class])
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.utils")
class UtilsModule {
    @Single
    fun connectivity(provider: ConnectivityProvider): Connectivity = provider.provide()
}
