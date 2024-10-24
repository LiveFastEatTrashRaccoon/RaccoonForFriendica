package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

interface PullNotificationManager {
    val isBackgroundCheckSupported: Boolean
    val isBackgroundRestricted: Boolean

    fun setPeriod(minutes: Long)

    fun start()

    fun stop()

    fun cancelAll()
}
