package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.UriHandler

@Stable
interface CustomUriHandler : UriHandler {
    fun openUri(
        uri: String,
        allowOpenExternal: Boolean,
    )
}
