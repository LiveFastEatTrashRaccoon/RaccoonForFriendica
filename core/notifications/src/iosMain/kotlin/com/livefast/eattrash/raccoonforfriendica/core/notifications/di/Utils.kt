package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getNotificationCenter(): NotificationCenter = CoreNotificationsDiHelper.notificationCenter

internal object CoreNotificationsDiHelper : KoinComponent {
    val notificationCenter by inject<NotificationCenter>()
}
