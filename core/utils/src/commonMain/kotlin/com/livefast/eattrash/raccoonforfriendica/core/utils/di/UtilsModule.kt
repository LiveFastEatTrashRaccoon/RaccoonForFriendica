package com.livefast.eattrash.raccoonforfriendica.core.utils.di

import com.livefast.eattrash.raccoonforfriendica.core.utils.network.ConnectivityProvider
import dev.jordond.connectivity.Connectivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
class UtilsModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideConnectivity(provider: ConnectivityProvider): Connectivity = provider.provide()
}
