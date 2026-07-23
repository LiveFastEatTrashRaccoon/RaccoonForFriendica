package com.livefast.eattrash.raccoonforfriendica.core.notifications.di

import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter

@Deprecated("Use the instance from dependency injection (e.g. as a constructor parameter)")
fun getNotificationCenter(): NotificationCenter = getByInjection(NotificationCenter::class)
