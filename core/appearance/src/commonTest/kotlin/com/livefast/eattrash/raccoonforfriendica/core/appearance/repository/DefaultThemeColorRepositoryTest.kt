package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultThemeColorRepositoryTest {
    private val sut = DefaultThemeColorRepository()

    @Test
    fun `when getColors then result is as expected`() {
        val res = sut.getColors()

        assertEquals(
            listOf(
                ThemeColor.Purple,
                ThemeColor.Blue,
                ThemeColor.LightBlue,
                ThemeColor.Green,
                ThemeColor.Yellow,
                ThemeColor.Orange,
                ThemeColor.Red,
                ThemeColor.Pink,
                ThemeColor.Gray,
                ThemeColor.White,
            ),
            res,
        )
    }
}
