package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.rememberStrings

val LocalStrings: ProvidableCompositionLocal<Strings> =
    staticCompositionLocalOf {
        error("CompositionLocal Strings not found")
    }

@Composable
fun ProvideStrings(lang: String, content: @Composable () -> Unit) {
    val strings = rememberStrings(lang)
    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalLayoutDirection provides lang.toLanguageDirection(),
        content = content,
    )
}
