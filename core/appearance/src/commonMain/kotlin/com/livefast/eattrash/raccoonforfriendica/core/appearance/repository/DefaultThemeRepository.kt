package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultThemeRepository : ThemeRepository {
    override val theme = MutableStateFlow<UiTheme>(UiTheme.Default)
    override val fontFamily = MutableStateFlow<UiFontFamily>(UiFontFamily.Default)
    override val fontScale = MutableStateFlow<UiFontScale>(UiFontScale.Normal)
    override val dynamicColors = MutableStateFlow(false)
    override val customSeedColor = MutableStateFlow<Color?>(null)

    override fun changeTheme(theme: UiTheme) {
        this.theme.update { theme }
    }

    override fun changeFontFamily(family: UiFontFamily) {
        fontFamily.update { family }
    }

    override fun changeFontScale(scale: UiFontScale) {
        fontScale.update { scale }
    }

    override fun changeDynamicColors(enabled: Boolean) {
        dynamicColors.update { enabled }
    }

    override fun changeCustomSeedColor(color: Color?) {
        customSeedColor.update { color }
    }

    override fun getCommentBarColor(depth: Int): Color {
        val colors = getCommentBarColors()
        if (colors.isEmpty()) {
            return Color.Transparent
        }
        val index = depth % colors.size
        return colors[index]
    }

    private fun getCommentBarColors(): List<Color> =
        buildList {
            this += Color(0xFF9400D3)
            this += Color(0xFF0000FF)
            this += Color(0xFF00FF00)
            this += Color(0xFFFFFF00)
            this += Color(0xFFFF7F00)
            this += Color(0xFFFF0000)
        }
}
