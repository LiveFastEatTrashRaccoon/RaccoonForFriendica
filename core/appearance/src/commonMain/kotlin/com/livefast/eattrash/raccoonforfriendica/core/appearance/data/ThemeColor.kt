package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface ThemeColor {
    data object Blue : ThemeColor

    data object LightBlue : ThemeColor

    data object Purple : ThemeColor

    data object Green : ThemeColor

    data object Red : ThemeColor

    data object Orange : ThemeColor

    data object Yellow : ThemeColor

    data object Pink : ThemeColor

    data object Gray : ThemeColor

    data object White : ThemeColor
}

fun ThemeColor.toColor(): Color =
    when (this) {
        ThemeColor.Blue -> Color(0xff034078)
        ThemeColor.LightBlue -> Color(0xff3bceac)
        ThemeColor.Purple -> Color(0xff540d6e)
        ThemeColor.Green -> Color(0xff0ead69)
        ThemeColor.Red -> Color(0xffd00000)
        ThemeColor.Orange -> Color(0xfff85e00)
        ThemeColor.Yellow -> Color(0xffffba08)
        ThemeColor.Pink -> Color(0xffee4266)
        ThemeColor.Gray -> Color(0xffadb6c4)
        ThemeColor.White -> Color(0xFFD7D7D7)
    }

@Composable
fun ThemeColor.toReadableName(): String =
    when (this) {
        ThemeColor.Blue -> LocalStrings.current.themeColorBlue
        ThemeColor.LightBlue -> LocalStrings.current.themeColorLightBlue
        ThemeColor.Purple -> LocalStrings.current.themeColorPurple
        ThemeColor.Green -> LocalStrings.current.themeColorGreen
        ThemeColor.Red -> LocalStrings.current.themeColorRed
        ThemeColor.Orange -> LocalStrings.current.themeColorOrange
        ThemeColor.Yellow -> LocalStrings.current.themeColorYellow
        ThemeColor.Gray -> LocalStrings.current.themeColorGray
        ThemeColor.Pink -> LocalStrings.current.themeColorPink
        ThemeColor.White -> LocalStrings.current.themeColorWhite
    }

@Composable
fun ThemeColor.toEmoji(): String =
    when (this) {
        ThemeColor.Blue -> "\uD83D\uDC33"
        ThemeColor.LightBlue -> "\uD83D\uDC2C"
        ThemeColor.Purple -> "\uD83D\uDC19"
        ThemeColor.Green -> "\uD83D\uDC38"
        ThemeColor.Red -> "\uD83E\uDD80"
        ThemeColor.Orange -> "\uD83E\uDD8A"
        ThemeColor.Yellow -> "\uD83E\uDD94"
        ThemeColor.Gray -> "\uD83E\uDD9D"
        ThemeColor.Pink -> "\uD83E\uDD84"
        ThemeColor.White -> "\uD83D\uDC3B\u200D❄\uFE0F"
    }
