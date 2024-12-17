package com.livefast.eattrash.raccoonforfriendica.core.l10n

import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultL10nManagerTest {
    private val sut = DefaultL10nManager()

    @Test
    fun `when initial then result is as expected`() {
        val res = sut.lang.value

        assertEquals(Locales.EN, res)
    }

    @Test
    fun `when changeLanguage then result is as expected`() {
        sut.changeLanguage(Locales.IT)

        val res = sut.lang.value

        assertEquals(Locales.IT, res)
    }
}
