package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator.DefaultNotificationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator.NotificationCoordinator
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val pushNotificationsModule =
    DI.Module("PushNotificationsModule") {
        import(nativePushNotificationsModule)
        bindSingleton<NotificationCoordinator> {
            DefaultNotificationCoordinator(
                settingsRepository = instance(),
                inboxManager = instance(),
                pullNotificationManager = instance(),
                pushNotificationManager = instance(),
            )
        }
    }
