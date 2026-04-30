package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import com.livefast.eattrash.raccoonforfriendica.core.notifications.DefaultNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val notificationsModule =
    DI.Module("NotificationsModule") {
        bindSingleton<NotificationCenter> {
            DefaultNotificationCenter()
        }
    }
