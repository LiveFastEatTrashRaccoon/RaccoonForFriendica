package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.CommentBarTheme
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ThemeRepository {
    val theme: StateFlow<UiTheme>
    val fontFamily: StateFlow<UiFontFamily>
    val fontScale: StateFlow<UiFontScale>
    val dynamicColors: StateFlow<Boolean>
    val customSeedColor: StateFlow<Color?>
    val commentBarTheme: StateFlow<CommentBarTheme>

    fun changeTheme(theme: UiTheme)

    fun changeFontFamily(family: UiFontFamily)

    fun changeFontScale(scale: UiFontScale)

    fun changeDynamicColors(enabled: Boolean)

    fun changeCustomSeedColor(color: Color?)

    fun changeCommentBarTheme(theme: CommentBarTheme)

    fun getCommentBarColor(depth: Int): Color

    fun getCommentBarColors(commentBarTheme: CommentBarTheme): List<Color>
}
