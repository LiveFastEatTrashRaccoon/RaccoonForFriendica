package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import androidx.compose.ui.graphics.Color
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
}
