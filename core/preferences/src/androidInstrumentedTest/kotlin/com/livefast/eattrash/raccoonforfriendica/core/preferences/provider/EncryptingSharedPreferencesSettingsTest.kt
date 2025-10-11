package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class EncryptingSharedPreferencesSettingsTest {

    private val preferences = ApplicationProvider.getApplicationContext<Application>().getSharedPreferences(
        "test_prefs",
        Context.MODE_PRIVATE,
    )

    private val sut = EncryptingSharedPreferencesSettings(
        preferences = preferences,
        encryptionHelper = DefaultEncryptionHelper(),
    )

    @AfterTest
    fun teardown() {
        preferences.edit().clear().apply()
    }

    @Test
    fun givenKeyExists_whenGetString_thenResultIsAsExpected() {
        val key = "key"
        val expectedValue = "test"
        sut.putString(key, expectedValue)

        val res = sut.getString(key, "default")

        assertEquals(expectedValue, res)
    }

    @Test
    fun givenKeyDoesNotExist_whenGetString_thenResultIsAsExpected() {
        val key = "key"
        val defaultValue = "default"

        val res = sut.getString(key, defaultValue)

        assertEquals(defaultValue, res)
    }

    @Test
    fun givenKeyExists_whenGetStringOrNull_thenResultIsAsExpected() {
        val key = "key"
        val expectedValue = "test"
        sut.putString(key, expectedValue)

        val res = sut.getStringOrNull(key)

        assertEquals(expectedValue, res)
    }

    @Test
    fun givenKeyDoesNotExist_whenGetStringOrNull_thenResultIsAsExpected() {
        val key = "key"

        val res = sut.getStringOrNull(key)

        assertNull(res)
    }
}
