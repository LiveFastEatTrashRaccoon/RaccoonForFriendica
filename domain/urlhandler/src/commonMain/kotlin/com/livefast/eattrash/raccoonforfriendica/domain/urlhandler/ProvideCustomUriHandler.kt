package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun ProvideCustomUriHandler(uriHandler: CustomUriHandler, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalUriHandler provides uriHandler,
        content = content,
    )
}
