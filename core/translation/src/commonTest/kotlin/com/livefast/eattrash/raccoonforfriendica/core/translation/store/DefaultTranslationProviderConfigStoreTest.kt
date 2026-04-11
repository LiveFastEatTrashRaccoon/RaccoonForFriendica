package com.livefast.eattrash.raccoonforfriendica.core.translation.store

import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultTranslationProviderConfigStoreTest {
    private val keyStore = mock<TemporaryKeyStore>(mode = MockMode.autofill)
    private val sut = DefaultTranslationProviderConfigStore(keyStore = keyStore)

    @Test
    fun `given no stored ids when getAll then return empty list`() = runTest {
        everySuspend { keyStore.get(key = any(), default = any<List<String>>(), any()) } returns emptyList()

        val result = sut.getAll()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `given stored ids when getAll then return configs`() = runTest {
        val id = "1"
        val config = TranslationProviderConfig(id = id, name = "LibreTranslate", url = "http://localhost")
        val json = """{"id":"$id","name":"LibreTranslate","url":"http://localhost"}"""

        everySuspend {
            keyStore.get(key = "translation_provider_config.index", default = any<List<String>>(), any())
        } returns
            listOf(id)
        everySuspend { keyStore.get(key = "translation_provider_config.default_id", default = "") } returns ""
        everySuspend { keyStore.get(key = "translation_provider_config.$id", default = "{}") } returns json

        val result = sut.getAll()

        assertEquals(1, result.size)
        assertEquals(config, result.first())
    }

    @Test
    fun `given existing id when getById then return config`() = runTest {
        val id = "1"
        val config = TranslationProviderConfig(id = id, name = "LibreTranslate", url = "http://localhost")
        val json = """{"id":"$id","name":"LibreTranslate","url":"http://localhost"}"""

        everySuspend { keyStore.get(key = "translation_provider_config.default_id", default = "") } returns ""
        everySuspend { keyStore.get(key = "translation_provider_config.$id", default = "{}") } returns json

        val result = sut.getById(id)

        assertEquals(config, result)
    }

    @Test
    fun `given non existing id when getById then return null`() = runTest {
        val id = "non-existing"
        everySuspend { keyStore.get(key = "translation_provider_config.default_id", default = "") } returns ""
        everySuspend { keyStore.get(key = "translation_provider_config.$id", default = "{}") } returns "{}"

        val result = sut.getById(id)

        assertNull(result)
    }

    @Test
    fun `when setDefaultId then verify keystore interaction`() = runTest {
        val id = "1"
        everySuspend { keyStore.get(key = any(), default = any<List<String>>(), any()) } returns emptyList()

        sut.setDefaultId(id)

        verifySuspend {
            keyStore.save("translation_provider_config.default_id", id)
        }
    }

    @Test
    fun `when delete then verify keystore interaction`() = runTest {
        val id = "1"
        everySuspend {
            keyStore.get(key = "translation_provider_config.index", default = any<List<String>>(), any())
        } returns
            listOf(id)

        sut.delete(id)

        verifySuspend {
            keyStore.remove("translation_provider_config.$id")
            keyStore.save("translation_provider_config.index", emptyList<String>(), ", ")
        }
    }
}
