package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

import org.koin.core.annotation.Single

@Single
internal actual class DefaultPullNotificationManager : PullNotificationManager {
    actual override val isSupported = false
    actual override val isBackgroundRestricted = false

    actual override fun setPeriod(minutes: Long) {
        // NO-OP
    }

    actual override fun start() {
        // NO-OP
    }

    actual override fun stop() {
        // NO-OP
    }

    actual override fun cancelAll() {
        // NO-OP
    }

    actual override fun oneshotCheck() {
        // NO-OP
    }
}
