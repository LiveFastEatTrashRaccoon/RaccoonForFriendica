package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.di

import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator.DefaultNotificationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator.NotificationCoordinator
import org.koin.dsl.module

val pushNotificationsModule = module {
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
