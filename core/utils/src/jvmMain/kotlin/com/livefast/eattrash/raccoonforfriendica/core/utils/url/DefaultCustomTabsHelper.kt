package com.livefast.eattrash.raccoonforfriendica.core.utils.url

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultCustomTabsHelper : CustomTabsHelper {
    override val isSupported = false

    override fun handle(url: String) {
        // no-op
    }
}
