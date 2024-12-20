package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.DefaultPushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal actual val nativePushNotificationsModule =
    DI.Module("NativePushNotificationsModule") {
        bind<PushNotificationManager> {
            singleton {
                DefaultPushNotificationManager()
            }
        }
    }
