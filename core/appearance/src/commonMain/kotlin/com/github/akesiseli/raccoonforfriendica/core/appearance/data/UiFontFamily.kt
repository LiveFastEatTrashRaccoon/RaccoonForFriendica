package com.github.akesiseli.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.github.akesiseli.raccoonforfriendica.core.l10n.messages.LocalStrings

enum class UiFontFamily {
    Exo2,
    NotoSans,
    Default,
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
