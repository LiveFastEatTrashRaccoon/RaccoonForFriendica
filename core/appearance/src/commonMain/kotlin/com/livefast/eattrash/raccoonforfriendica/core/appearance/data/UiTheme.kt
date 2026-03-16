package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources

sealed interface UiTheme {
    data object Light : UiTheme

    data object Dark : UiTheme

    data object Black : UiTheme

    data object Default : UiTheme
}

fun Int.toUiTheme(): UiTheme = when (this) {
    3 -> UiTheme.Black
    2 -> UiTheme.Dark
    1 -> UiTheme.Light
    else -> UiTheme.Default
}

fun UiTheme.toInt(): Int = when (this) {
    UiTheme.Black -> 3
    UiTheme.Dark -> 2
    UiTheme.Light -> 1
    else -> 0
}

@Composable
fun UiTheme?.toReadableName(): String = when (this) {
    UiTheme.Black -> LocalStrings.current.settingsThemeBlack
    UiTheme.Dark -> LocalStrings.current.settingsThemeDark
    UiTheme.Light -> LocalStrings.current.settingsThemeLight
    else -> LocalStrings.current.systemDefault
}

@Composable
fun UiTheme.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    UiTheme.Black -> coreResources.darkModeFill
    UiTheme.Dark -> coreResources.darkMode
    UiTheme.Light -> coreResources.lightMode
    else -> coreResources.computer
}
