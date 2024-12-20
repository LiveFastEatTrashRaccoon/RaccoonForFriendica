package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator.DefaultNotificationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator.NotificationCoordinator
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val pushNotificationsModule =
    DI.Module("PushNotificationsModule") {
        import(nativePushNotificationsModule)
        bind<NotificationCoordinator> {
            singleton {
                DefaultNotificationCoordinator(
                    settingsRepository = instance(),
                    inboxManager = instance(),
                    pullNotificationManager = instance(),
                    pushNotificationManager = instance(),
                )
            }
        }
    }
