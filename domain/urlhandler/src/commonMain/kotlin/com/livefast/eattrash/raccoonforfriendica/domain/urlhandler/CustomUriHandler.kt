package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.UriHandler

@Stable
interface CustomUriHandler : UriHandler {
    fun openUri(uri: String, allowOpenExternal: Boolean = true, allowOpenInternal: Boolean = true)
}

interface CustomUriHandlerFactory {
    fun create(fallbackHandler: UriHandler): CustomUriHandler
}

fun UriHandler.openExternally(uri: String) {
    if (this is CustomUriHandler) {
        openUri(uri = uri, allowOpenInternal = false)
    } else {
        openUri(uri)
    }
}

fun UriHandler.openInternally(uri: String) {
    if (this is CustomUriHandler) {
        openUri(uri = uri, allowOpenExternal = false)
    } else {
        openUri(uri)
    }
}
