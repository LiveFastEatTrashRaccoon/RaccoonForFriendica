package com.livefast.eattrash.raccoonforfriendica

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.di.RootGraph
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationComponent
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.PushNotificationComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders

@DependencyGraph(scope = AppScope::class)
interface AndroidRootGraph :
    RootGraph,
    MetroAppComponentProviders,
    PushNotificationComponent,
    PullNotificationComponent {

    @Provides
    fun provideContext(): Context = MainApplication.instance
}
