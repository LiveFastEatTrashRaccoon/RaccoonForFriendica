package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultThemeRepository : ThemeRepository {
    override val uiTheme = MutableStateFlow<UiTheme?>(null)
    override val fontFamily = MutableStateFlow<UiFontFamily>(UiFontFamily.Exo2)
    override val dynamicColors = MutableStateFlow(false)
    override val customSeedColor = MutableStateFlow<Color?>(null)

    override fun changeUiTheme(theme: UiTheme?) {
        uiTheme.update { theme }
    }

    override fun changeFontFamily(family: UiFontFamily) {
        fontFamily.update { family }
    }

    override fun changeDynamicColors(enabled: Boolean) {
        dynamicColors.update { enabled }
    }

    override fun changeCustomSeedColor(color: Color?) {
        customSeedColor.update { color }
    }
}
