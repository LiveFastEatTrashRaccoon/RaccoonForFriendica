package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

internal class DefaultPullNotificationManager : PullNotificationManager {
    override val isSupported = false
    override val isBackgroundRestricted = false

    override fun setPeriod(minutes: Long) {
        // NO-OP
    }

    override fun start() {
        // NO-OP
    }

    override fun stop() {
        // NO-OP
    }

    override fun cancelAll() {
        // NO-OP
    }

    override fun oneshotCheck() {
        // NO-OP
    }
}
