package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.DefaultPushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import org.koin.dsl.module

internal actual val nativePushNotificationsModule = module {
    single<PushNotificationManager> {
        DefaultPushNotificationManager()
    }
}
