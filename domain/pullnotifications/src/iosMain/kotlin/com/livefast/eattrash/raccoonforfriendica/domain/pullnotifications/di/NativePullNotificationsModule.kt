package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.DefaultPullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal actual val nativePullNotificationsModule =
    DI.Module("NativePullNotificationsModule") {
        bind<PullNotificationManager> { singleton { DefaultPullNotificationManager() } }
    }
