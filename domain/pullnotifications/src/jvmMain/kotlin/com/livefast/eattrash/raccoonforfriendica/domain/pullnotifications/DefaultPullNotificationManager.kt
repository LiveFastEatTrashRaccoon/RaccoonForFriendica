package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultPullNotificationManager : PullNotificationManager {
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
