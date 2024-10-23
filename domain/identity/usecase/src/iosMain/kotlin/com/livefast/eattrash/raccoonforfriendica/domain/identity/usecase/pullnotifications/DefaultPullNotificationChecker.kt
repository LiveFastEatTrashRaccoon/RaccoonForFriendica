package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications

class DefaultPullNotificationChecker : PullNotificationChecker {
    override val isBackgroundCheckSupported = false
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
}
