package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import org.koin.java.KoinJavaComponent

actual fun getNotificationCenter(): NotificationCenter {
    val res by KoinJavaComponent.inject<NotificationCenter>(NotificationCenter::class.java)
    return res
}
