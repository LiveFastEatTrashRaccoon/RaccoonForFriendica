package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.DefaultNotificationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.NotificationCoordinator
import org.koin.dsl.module

val domainPushNotificationsModule =
    module {
        includes(nativePushNotificationsModule)

        single<NotificationCoordinator> {
            DefaultNotificationCoordinator(
                settingsRepository = get(),
                inboxManager = get(),
                pullNotificationManager = get(),
                pushNotificationManager = get(),
            )
        }
    }
