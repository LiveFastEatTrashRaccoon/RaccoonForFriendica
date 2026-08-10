package com.livefast.eattrash.raccoonforfriendica.core.utils.url

import org.koin.core.annotation.Single

@Single
internal class DefaultCustomTabsHelper : CustomTabsHelper {
    override val isSupported = false

    override fun handle(url: String) {
        // no-op
    }
}
