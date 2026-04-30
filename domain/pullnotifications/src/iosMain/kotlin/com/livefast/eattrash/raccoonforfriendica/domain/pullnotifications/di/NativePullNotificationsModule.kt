package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.DefaultPullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import org.kodein.di.DI
import org.kodein.di.bindSingleton

internal actual val nativePullNotificationsModule =
    DI.Module("NativePullNotificationsModule") {
        bindSingleton<PullNotificationManager> {
            DefaultPullNotificationManager()
        }
    }
