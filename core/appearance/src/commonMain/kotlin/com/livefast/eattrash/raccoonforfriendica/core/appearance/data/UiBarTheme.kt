package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface UiBarTheme {
    data object Solid : UiBarTheme

    data object Transparent : UiBarTheme

    data object Opaque : UiBarTheme
}

fun UiBarTheme?.toInt(): Int =
    when (this) {
        UiBarTheme.Transparent -> 2
        UiBarTheme.Opaque -> 1
        else -> 0
    }

@Composable
fun UiBarTheme?.toReadableName(): String =
    when (this) {
        UiBarTheme.Transparent -> LocalStrings.current.barThemeTransparent
        UiBarTheme.Opaque -> LocalStrings.current.barThemeOpaque
        else -> ""
    }
