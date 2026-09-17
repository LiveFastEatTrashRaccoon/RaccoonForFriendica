package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.DefaultEncryptionHelper
import com.livefast.eattrash.raccoonforfriendica.core.preferences.provider.EncryptingSettings
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.Foundation.NSUserDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EncryptingSettingsTest {

    private val encryptionHelper = DefaultEncryptionHelper()
    private val underlyingSettings = NSUserDefaultsSettings(
        delegate = NSUserDefaults(suiteName = "test_encrypting_settings_suite"),
    )
    private val sut = EncryptingSettings(
        settings = underlyingSettings,
        encryptionHelper = encryptionHelper,
    )

    @Test
    fun `given a string setting when saved then it should be stored encrypted in underlying settings`() {
        val key = "secret_key"
        val value = "my_secret_password"

        sut.putString(key, value)

        val rawInUnderlying = underlyingSettings.getString(key, "")
        assertNotEquals(value, rawInUnderlying)
        assertTrue(rawInUnderlying.isNotEmpty())
    }

    @Test
    fun `given a string setting when retrieved then it should be properly decrypted`() {
        val key = "secret_key"
        val value = "my_secret_password"

        sut.putString(key, value)

        val retrieved = sut.getString(key, "")
        assertEquals(value, retrieved)
    }

    @Test
    fun `given a boolean setting when saved and retrieved then it should match`() {
        val key = "bool_key"

        sut.putBoolean(key, value = true)
        assertTrue(sut.getBoolean(key, defaultValue = false))

        sut.putBoolean(key, value = false)
        assertFalse(sut.getBoolean(key, defaultValue = true))
    }

    @Test
    fun `given a string listener when value changes then listener should receive decrypted value`() {
        val key = "observed_key"
        var notifiedValue: String? = null

        val listener = sut.addStringListener(key, "") { newValue ->
            notifiedValue = newValue
        }

        sut.putString(key, "new_value")

        assertEquals("new_value", notifiedValue)
        listener.deactivate()
    }

    @Test
    fun `given a boolean listener when value changes then listener should receive new value`() {
        val key = "observed_bool"
        var notifiedValue: Boolean? = null

        val listener = sut.addBooleanListener(key, defaultValue = false) { newValue ->
            notifiedValue = newValue
        }

        sut.putBoolean(key, value = true)

        assertEquals(true, notifiedValue)
        listener.deactivate()
    }
}
