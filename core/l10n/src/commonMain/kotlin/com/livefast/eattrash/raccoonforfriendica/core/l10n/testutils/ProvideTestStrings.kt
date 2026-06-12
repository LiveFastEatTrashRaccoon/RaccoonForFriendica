package com.livefast.eattrash.raccoonforfriendica.core.l10n.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
fun ProvideTestStrings(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalStrings provides MockStrings(),
        content = content,
    )
}
