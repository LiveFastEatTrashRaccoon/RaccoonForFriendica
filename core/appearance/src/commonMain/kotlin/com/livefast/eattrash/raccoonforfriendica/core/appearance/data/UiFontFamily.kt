package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface UiFontFamily {
    data object Exo2 : UiFontFamily

    data object NotoSans : UiFontFamily

    data object Default : UiFontFamily
}

fun Int.toUiFontFamily() =
    when (this) {
        1 -> UiFontFamily.Exo2
        2 -> UiFontFamily.NotoSans
        else -> UiFontFamily.Default
    }

fun UiFontFamily.toInt() =
    when (this) {
        UiFontFamily.Exo2 -> 1
        UiFontFamily.NotoSans -> 2
        else -> 0
    }

@Composable
fun UiFontFamily.toReadableName() =
    when (this) {
        UiFontFamily.Exo2 -> "Exo2"
        UiFontFamily.NotoSans -> "Noto Sans"
        UiFontFamily.Default -> LocalStrings.current.systemDefault
    }
