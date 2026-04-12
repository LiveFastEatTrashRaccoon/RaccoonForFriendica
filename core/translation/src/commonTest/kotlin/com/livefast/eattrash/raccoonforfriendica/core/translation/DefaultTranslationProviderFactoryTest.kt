package com.livefast.eattrash.raccoonforfriendica.core.translation

import com.livefast.eattrash.raccoonforfriendica.core.translation.libretranslate.LibreTranslateProvider
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class DefaultTranslationProviderFactoryTest {
    private val sut = DefaultTranslationProviderFactory()

    @Test
    fun `given LibreTranslate config when create then return LibreTranslateProvider`() {
        val config = TranslationProviderConfig(
            name = TranslationProviderTypes.LibreTranslate.name,
            url = "https://libretranslate.com",
            apiKey = "fake-api-key",
        )

        val result = sut.create(config)

        assertIs<LibreTranslateProvider>(result)
    }

    @Test
    fun `given unknown config when create then throw IllegalArgumentException`() {
        val config = TranslationProviderConfig(
            name = "UnknownProvider",
            url = "https://example.com",
        )

        assertFailsWith<IllegalArgumentException> {
            sut.create(config)
        }
    }
}
