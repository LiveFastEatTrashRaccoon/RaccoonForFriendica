package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
import kotlinx.coroutines.runBlocking
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

class EncryptingPreferencesSettings(
    private val preferences: Preferences,
    private val encryptionHelper: EncryptionHelper,
) : ObservableSettings {

    override fun addBooleanListener(
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit,
    ): SettingsListener = addListener(key) { callback(getBoolean(key, defaultValue)) }

    override fun addBooleanOrNullListener(key: String, callback: (Boolean?) -> Unit): SettingsListener =
        addListener(key) { callback(getBooleanOrNull(key)) }

    override fun addDoubleListener(key: String, defaultValue: Double, callback: (Double) -> Unit): SettingsListener =
        addListener(key) { callback(getDouble(key, defaultValue)) }

    override fun addDoubleOrNullListener(key: String, callback: (Double?) -> Unit): SettingsListener =
        addListener(key) { callback(getDoubleOrNull(key)) }

    override fun addFloatListener(key: String, defaultValue: Float, callback: (Float) -> Unit): SettingsListener =
        addListener(key) { callback(getFloat(key, defaultValue)) }

    override fun addFloatOrNullListener(key: String, callback: (Float?) -> Unit): SettingsListener =
        addListener(key) { callback(getFloatOrNull(key)) }

    override fun addIntListener(key: String, defaultValue: Int, callback: (Int) -> Unit): SettingsListener =
        addListener(key) { callback(getInt(key, defaultValue)) }

    override fun addIntOrNullListener(key: String, callback: (Int?) -> Unit): SettingsListener =
        addListener(key) { callback(getIntOrNull(key)) }

    override fun addLongListener(key: String, defaultValue: Long, callback: (Long) -> Unit): SettingsListener =
        addListener(key) { callback(getLong(key, defaultValue)) }

    override fun addLongOrNullListener(key: String, callback: (Long?) -> Unit): SettingsListener =
        addListener(key) { callback(getLongOrNull(key)) }

    override fun addStringListener(key: String, defaultValue: String, callback: (String) -> Unit): SettingsListener =
        addListener(key) { callback(getString(key, defaultValue)) }

    override fun addStringOrNullListener(key: String, callback: (String?) -> Unit): SettingsListener =
        addListener(key) { callback(getStringOrNull(key)) }

    override fun clear() {
        preferences.apply { clear() }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        check(hasKey(key)) { return defaultValue }
        return preferences.getBoolean(key, false)
    }

    override fun getBooleanOrNull(key: String): Boolean? {
        check(hasKey(key)) { return null }
        return preferences.getBoolean(key, false)
    }

    override fun getDouble(key: String, defaultValue: Double): Double {
        check(hasKey(key)) { return defaultValue }
        return Double.fromBits(preferences.getLong(key, 0.0.toRawBits()))
    }

    override fun getDoubleOrNull(key: String): Double? {
        check(hasKey(key)) { return null }
        return Double.fromBits(preferences.getLong(key, 0.0.toRawBits()))
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        check(hasKey(key)) { return defaultValue }
        return preferences.getFloat(key, 0.0f)
    }

    override fun getFloatOrNull(key: String): Float? {
        check(hasKey(key)) { return null }
        return preferences.getFloat(key, 0.0f)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        check(hasKey(key)) { return defaultValue }
        return preferences.getInt(key, 0)
    }

    override fun getIntOrNull(key: String): Int? {
        check(hasKey(key)) { return null }
        return preferences.getInt(key, 0)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        check(hasKey(key)) { return defaultValue }
        return preferences.getLong(key, 0)
    }

    override fun getLongOrNull(key: String): Long? {
        check(hasKey(key)) { return null }
        return preferences.getLong(key, 0)
    }

    override fun getString(key: String, defaultValue: String): String {
        check(hasKey(key)) { return defaultValue }
        return decrypt(preferences.get(key, "").orEmpty())
    }

    override fun getStringOrNull(key: String): String? {
        check(hasKey(key)) { return null }
        return decrypt(preferences.get(key, "").orEmpty())
    }

    override fun hasKey(key: String): Boolean = preferences.keys().contains(key)

    override fun putBoolean(key: String, value: Boolean) {
        preferences.putBoolean(key, value)
    }

    override fun putDouble(key: String, value: Double) {
        preferences.putLong(key, value.toRawBits())
    }

    override fun putFloat(key: String, value: Float) {
        preferences.putFloat(key, value)
    }

    override fun putInt(key: String, value: Int) {
        preferences.putInt(key, value)
    }

    override fun putLong(key: String, value: Long) {
        preferences.putLong(key, value)
    }

    override fun putString(key: String, value: String) {
        preferences.put(key, encrypt(value))
    }

    override fun remove(key: String) {
        preferences.remove(key)
    }

    override val keys: Set<String>
        get() = preferences.keys().toSet()

    override val size: Int
        get() = preferences.keys().size

    private fun decrypt(value: String): String = runBlocking {
        val bytes = encryptionHelper.decodeFromString(value)
        encryptionHelper.decrypt(bytes).orEmpty()
    }

    private fun encrypt(value: String): String = runBlocking {
        val bytes = encryptionHelper.encrypt(value) ?: byteArrayOf()
        encryptionHelper.encodeToString(bytes)
    }

    private fun addListener(key: String, callback: () -> Unit): SettingsListener {
        var prev = preferences.get(key, "")
        val prefsListener =
            PreferenceChangeListener { evt ->
                if (evt.key != key) {
                    return@PreferenceChangeListener
                }
                val current = evt.newValue
                if (prev != current) {
                    callback()
                    prev = current
                }
            }
        preferences.addPreferenceChangeListener(prefsListener)
        return CustomListener(preferences, prefsListener)
    }

    private class CustomListener(
        private val preferences: Preferences,
        private val listener: PreferenceChangeListener,
    ) : SettingsListener {
        override fun deactivate() {
            preferences.removePreferenceChangeListener(listener)
        }
    }
}
