package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import org.kodein.di.instance

fun getNotificationCenter(): NotificationCenter {
    val res by RootDI.di.instance<NotificationCenter>()
    return res
}
