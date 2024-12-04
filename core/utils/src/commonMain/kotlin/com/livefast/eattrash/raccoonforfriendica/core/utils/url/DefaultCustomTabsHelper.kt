package com.livefast.eattrash.raccoonforfriendica.core.utils.url

import org.koin.core.annotation.Single

@Single
internal expect class DefaultCustomTabsHelper : CustomTabsHelper {
    override val isSupported: Boolean

    override fun handle(url: String)
}
