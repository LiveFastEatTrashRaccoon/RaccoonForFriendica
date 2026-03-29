package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.CommentBarTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultThemeRepository : ThemeRepository {
    override val theme = MutableStateFlow<UiTheme>(UiTheme.Default)
    override val fontFamily = MutableStateFlow<UiFontFamily>(UiFontFamily.Default)
    override val fontScale = MutableStateFlow<UiFontScale>(UiFontScale.Normal)
    override val dynamicColors = MutableStateFlow(false)
    override val customSeedColor = MutableStateFlow<Color?>(null)
    override val commentBarTheme = MutableStateFlow<CommentBarTheme>(CommentBarTheme.Rainbow)

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

    override fun changeCommentBarTheme(theme: CommentBarTheme) {
        commentBarTheme.update { theme }
    }

    override fun getCommentBarColor(depth: Int): Color {
        val colors = getCommentBarColors(commentBarTheme.value)
        check(colors.isNotEmpty()) { return Color.Transparent }
        val index = depth % colors.size
        return colors[index]
    }

    override fun getCommentBarColors(commentBarTheme: CommentBarTheme): List<Color> = when (commentBarTheme) {
        CommentBarTheme.Green -> buildList {
            this += Color(0xFF1B4332)
            this += Color(0xFF2D6A4F)
            this += Color(0xFF40916C)
            this += Color(0xFF52B788)
            this += Color(0xFF74C69D)
            this += Color(0xFF95D5B2)
        }

        CommentBarTheme.Red -> buildList {
            this += Color(0xFF6A040F)
            this += Color(0xFF9D0208)
            this += Color(0xFFD00000)
            this += Color(0xFFDC2F02)
            this += Color(0xFFE85D04)
            this += Color(0xFFF48C06)
        }

        CommentBarTheme.Blue -> buildList {
            this += Color(0xFF012A4A)
            this += Color(0xFF013A63)
            this += Color(0xFF014F86)
            this += Color(0xFF2C7DA0)
            this += Color(0xFF61A5C2)
            this += Color(0xFFA9D6E5)
        }

        else -> buildList {
            this += Color(0xFF9400D3)
            this += Color(0xFF0000FF)
            this += Color(0xFF00FF00)
            this += Color(0xFFFFFF00)
            this += Color(0xFFFF7F00)
            this += Color(0xFFFF0000)
        }
    }
}
