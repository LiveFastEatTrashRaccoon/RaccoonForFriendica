package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.DefaultPullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import org.koin.dsl.module

actual val pullNotificationsNativeModule =
    module {
        single<PullNotificationManager> {
            DefaultPullNotificationManager()
        }
    }
