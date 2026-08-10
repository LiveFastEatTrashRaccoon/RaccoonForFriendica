package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

import org.koin.core.annotation.Single

@Single
internal actual class DefaultPullNotificationManager : PullNotificationManager {
    actual override val isSupported = false
    actual override val isBackgroundRestricted = false

    actual override fun setPeriod(minutes: Long) {
        // no-op
    }

    actual override fun start() {
        // no-op
    }

    actual override fun stop() {
        // no-op
    }

    actual override fun cancelAll() {
        // no-op
    }

    actual override fun oneshotCheck() {
        // no-op
    }
}
