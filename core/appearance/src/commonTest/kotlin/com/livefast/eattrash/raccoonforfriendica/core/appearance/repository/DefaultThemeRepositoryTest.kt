package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.CommentBarTheme
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class DefaultThemeRepositoryTest {
    private val sut = DefaultThemeRepository()

    @Test
    fun `when changeTheme then value is as expected`() {
        val resBefore = sut.theme.value
        assertEquals(UiTheme.Default, resBefore)

        sut.changeTheme(UiTheme.Dark)

        val resAfter = sut.theme.value
        assertEquals(UiTheme.Dark, resAfter)
    }

    @Test
    fun `when changeCommentBarTheme then value is as expected`() {
        val resBefore = sut.commentBarTheme.value
        assertEquals(CommentBarTheme.Rainbow, resBefore)

        sut.changeCommentBarTheme(CommentBarTheme.Blue)

        val resAfter = sut.commentBarTheme.value
        assertEquals(CommentBarTheme.Blue, resAfter)
    }

    @Test
    fun `when changeFontFamily then value is as expected`() {
        val resBefore = sut.fontFamily.value
        assertEquals(UiFontFamily.Default, resBefore)

        sut.changeFontFamily(UiFontFamily.NotoSans)

        val resAfter = sut.fontFamily.value
        assertEquals(UiFontFamily.NotoSans, resAfter)
    }

    @Test
    fun `when changeFontScale then value is as expected`() {
        val resBefore = sut.fontScale.value
        assertEquals(UiFontScale.Normal, resBefore)

        val value = UiFontScale.Larger
        sut.changeFontScale(value)

        val resAfter = sut.fontScale.value
        assertEquals(value, resAfter)
    }

    @Test
    fun `when changeDynamicColors then value is as expected`() {
        val initial = sut.dynamicColors.value
        sut.changeDynamicColors(!initial)

        val res = sut.dynamicColors.value
        assertNotEquals(initial, res)
    }

    @Test
    fun `when changeCustomSeedColor then value is as expected`() {
        val resBefore = sut.customSeedColor.value
        assertNull(resBefore)

        sut.changeCustomSeedColor(Color.Red)

        val resAfter = sut.customSeedColor.value
        assertEquals(Color.Red, resAfter)
    }

    @Test
    fun `when getCommentBarColors then value is as expected`() {
        val blueColors = sut.getCommentBarColors(CommentBarTheme.Blue)
        assertEquals(6, blueColors.size)
        assertEquals(Color(0xFF012A4A), blueColors[0])

        val greenColors = sut.getCommentBarColors(CommentBarTheme.Green)
        assertEquals(6, greenColors.size)
        assertEquals(Color(0xFF1B4332), greenColors[0])

        val redColors = sut.getCommentBarColors(CommentBarTheme.Red)
        assertEquals(6, redColors.size)
        assertEquals(Color(0xFF6A040F), redColors[0])

        val rainbowColors = sut.getCommentBarColors(CommentBarTheme.Rainbow)
        assertEquals(6, rainbowColors.size)
        assertEquals(Color(0xFF9400D3), rainbowColors[0])
    }

    @Test
    fun `when getCommentBarColor then value is as expected`() {
        sut.changeCommentBarTheme(CommentBarTheme.Blue)
        val colors = sut.getCommentBarColors(CommentBarTheme.Blue)

        assertEquals(colors[0], sut.getCommentBarColor(0))
        assertEquals(colors[1], sut.getCommentBarColor(1))
        assertEquals(colors[5], sut.getCommentBarColor(5))
        assertEquals(colors[0], sut.getCommentBarColor(6))
    }
}
