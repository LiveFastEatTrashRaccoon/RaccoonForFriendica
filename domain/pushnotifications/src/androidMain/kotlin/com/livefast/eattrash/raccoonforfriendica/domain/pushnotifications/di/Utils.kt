package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.DefaultPushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.PushNotificationManager
import org.koin.dsl.module

actual val nativePushNotificationsModule =
    module {
        single<PushNotificationManager> {
            DefaultPushNotificationManager(
                context = get(),
                pushNotificationRepository = get(),
                accountRepository = get(),
            )
        }
    }
