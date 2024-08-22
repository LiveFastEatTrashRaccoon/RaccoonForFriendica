package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.di.getCustomUriHandler

@Composable
fun ProvideCustomUriHandler(content: @Composable () -> Unit) {
    val fallbackHandler = LocalUriHandler.current
    val customUriHandler = remember { getCustomUriHandler(fallbackHandler) }
    CompositionLocalProvider(
        value = LocalUriHandler provides customUriHandler,
        content = content,
    )
}
