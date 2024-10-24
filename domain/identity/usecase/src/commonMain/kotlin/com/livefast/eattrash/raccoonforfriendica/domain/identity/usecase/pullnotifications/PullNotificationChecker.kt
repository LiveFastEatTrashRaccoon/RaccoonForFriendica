package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications

interface PullNotificationChecker {
    val isBackgroundCheckSupported: Boolean
    val isBackgroundRestricted: Boolean

    fun setPeriod(minutes: Long)

    fun start()

    fun stop()
}
