package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

interface PullNotificationManager {
    val isSupported: Boolean
    val isBackgroundRestricted: Boolean

    fun setPeriod(minutes: Long)

    fun start()

    fun oneshotCheck()

    fun stop()

    fun cancelAll()
}
