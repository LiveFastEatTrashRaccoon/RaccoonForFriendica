package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

internal class DefaultPullNotificationManager : PullNotificationManager {
    override val isSupported = false
    override val isBackgroundRestricted = false

    override fun setPeriod(minutes: Long) {
        // no-op
    }

    override fun start() {
        // no-op
    }

    override fun stop() {
        // no-op
    }

    override fun cancelAll() {
        // no-op
    }

    override fun oneshotCheck() {
        // no-op
    }
}
