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
        UiBarTheme.Solid -> 1
        UiBarTheme.Opaque -> 2
        else -> 0
    }

fun Int.toUIBarTheme(): UiBarTheme =
    when (this) {
        1 -> UiBarTheme.Solid
        2 -> UiBarTheme.Opaque
        else -> UiBarTheme.Transparent
    }

@Composable
fun UiBarTheme?.toReadableName(): String =
    when (this) {
        UiBarTheme.Transparent -> LocalStrings.current.barThemeTransparent
        UiBarTheme.Opaque -> LocalStrings.current.barThemeOpaque
        else -> LocalStrings.current.barThemeSolid
    }
