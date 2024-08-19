package com.livefast.eattrash.raccoonforfriendica.core.utils.url

interface CustomTabsHelper {
    val isSupported: Boolean

    fun handle(url: String)
}
