package com.livefast.eattrash.raccoonforfriendica.core.utils.url

class DefaultCustomTabsHelper : CustomTabsHelper {
    override val isSupported = false

    override fun handle(url: String) {
        // no-op
    }
}
