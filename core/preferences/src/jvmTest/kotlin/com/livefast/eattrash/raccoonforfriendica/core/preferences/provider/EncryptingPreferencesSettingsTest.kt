package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import java.util.prefs.Preferences
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EncryptingPreferencesSettingsTest {

    private val preferences = Preferences.userRoot().node("test_node")

    private val sut = EncryptingPreferencesSettings(
        preferences = preferences,
        encryptionHelper = DefaultEncryptionHelper(),
    )

    @AfterTest
    fun teardown() {
        preferences.clear()
    }

    @Test
    fun `given key exists, when getString, then result is as expected`() {
        val key = "key"
        val expectedValue = "test"
        sut.putString(key, expectedValue)

        val res = sut.getString(key, "default")

        assertEquals(expectedValue, res)
    }

    @Test
    fun `given key does not exist, when getString, then result is as expected`() {
        val key = "key"
        val defaultValue = "default"

        val res = sut.getString(key, defaultValue)

        assertEquals(defaultValue, res)
    }

    @Test
    fun `given key exists, when getStringOrNull, then result is as expected`() {
        val key = "key"
        val expectedValue = "test"
        sut.putString(key, expectedValue)

        val res = sut.getStringOrNull(key)

        assertEquals(expectedValue, res)
    }

    @Test
    fun `given key does not exist, when getStringOrNull, then result is as expected`() {
        val key = "key"

        val res = sut.getStringOrNull(key)

        assertNull(res)
    }
}
