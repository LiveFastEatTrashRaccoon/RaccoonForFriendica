package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import com.livefast.eattrash.raccoonforfriendica.core.notifications.DefaultNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import org.koin.dsl.module

val coreNotificationsModule =
    module {
        single<NotificationCenter> {
            DefaultNotificationCenter()
        }
    }
