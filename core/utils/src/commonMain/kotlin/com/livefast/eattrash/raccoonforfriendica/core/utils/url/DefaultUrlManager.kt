package com.livefast.eattrash.raccoonforfriendica.core.utils.url

import androidx.compose.ui.platform.UriHandler

internal class DefaultUrlManager(
    private val defaultHandler: UriHandler,
) : UrlManager {
    override fun open(uri: String) {
        defaultHandler.openUri(uri)
    }
}
