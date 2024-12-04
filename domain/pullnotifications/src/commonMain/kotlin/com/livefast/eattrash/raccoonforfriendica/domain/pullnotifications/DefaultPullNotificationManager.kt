package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

import org.koin.core.annotation.Single

@Single
internal expect class DefaultPullNotificationManager : PullNotificationManager {
    override val isSupported: Boolean
    override val isBackgroundRestricted: Boolean

    override fun setPeriod(minutes: Long)

    override fun start()

    override fun oneshotCheck()

    override fun stop()

    override fun cancelAll()
}
