package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun ProvideCustomUriHandler(content: @Composable () -> Unit) {
    val fallbackHandler = LocalUriHandler.current
    val customUriHandler: CustomUriHandler = koinInject(parameters = { parametersOf(fallbackHandler) })
    CompositionLocalProvider(
        value = LocalUriHandler provides customUriHandler,
        content = content,
    )
}
