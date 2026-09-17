package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener

class EncryptingSettings(private val settings: Settings, private val encryptionHelper: EncryptionHelper) :
    ObservableSettings {

    private val listeners = mutableMapOf<String, MutableList<() -> Unit>>()

    override fun addBooleanListener(
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit,
    ): SettingsListener = addGenericListener(key) { callback(getBoolean(key, defaultValue)) }

    override fun addBooleanOrNullListener(key: String, callback: (Boolean?) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getBooleanOrNull(key)) }

    override fun addDoubleListener(key: String, defaultValue: Double, callback: (Double) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getDouble(key, defaultValue)) }

    override fun addDoubleOrNullListener(key: String, callback: (Double?) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getDoubleOrNull(key)) }

    override fun addFloatListener(key: String, defaultValue: Float, callback: (Float) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getFloat(key, defaultValue)) }

    override fun addFloatOrNullListener(key: String, callback: (Float?) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getFloatOrNull(key)) }

    override fun addIntListener(key: String, defaultValue: Int, callback: (Int) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getInt(key, defaultValue)) }

    override fun addIntOrNullListener(key: String, callback: (Int?) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getIntOrNull(key)) }

    override fun addLongListener(key: String, defaultValue: Long, callback: (Long) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getLong(key, defaultValue)) }

    override fun addLongOrNullListener(key: String, callback: (Long?) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getLongOrNull(key)) }

    override fun addStringListener(key: String, defaultValue: String, callback: (String) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getString(key, defaultValue)) }

    override fun addStringOrNullListener(key: String, callback: (String?) -> Unit): SettingsListener =
        addGenericListener(key) { callback(getStringOrNull(key)) }

    override fun clear() {
        val keysToNotify = listeners.keys.toList()
        settings.clear()
        keysToNotify.forEach { notifyListeners(it) }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = settings.getBoolean(key, defaultValue)

    override fun getBooleanOrNull(key: String): Boolean? = settings.getBooleanOrNull(key)

    override fun getDouble(key: String, defaultValue: Double): Double = settings.getDouble(key, defaultValue)

    override fun getDoubleOrNull(key: String): Double? = settings.getDoubleOrNull(key)

    override fun getFloat(key: String, defaultValue: Float): Float = settings.getFloat(key, defaultValue)

    override fun getFloatOrNull(key: String): Float? = settings.getFloatOrNull(key)

    override fun getInt(key: String, defaultValue: Int): Int = settings.getInt(key, defaultValue)

    override fun getIntOrNull(key: String): Int? = settings.getIntOrNull(key)

    override fun getLong(key: String, defaultValue: Long): Long = settings.getLong(key, defaultValue)

    override fun getLongOrNull(key: String): Long? = settings.getLongOrNull(key)

    override fun getString(key: String, defaultValue: String): String {
        if (!settings.hasKey(key)) return defaultValue
        val raw = settings.getStringOrNull(key) ?: return defaultValue
        return decrypt(raw).ifEmpty { defaultValue }
    }

    override fun getStringOrNull(key: String): String? {
        if (!settings.hasKey(key)) return null
        val raw = settings.getStringOrNull(key) ?: return null
        return decrypt(raw)
    }

    override fun hasKey(key: String): Boolean = settings.hasKey(key)

    override fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
        notifyListeners(key)
    }

    override fun putDouble(key: String, value: Double) {
        settings.putDouble(key, value)
        notifyListeners(key)
    }

    override fun putFloat(key: String, value: Float) {
        settings.putFloat(key, value)
        notifyListeners(key)
    }

    override fun putInt(key: String, value: Int) {
        settings.putInt(key, value)
        notifyListeners(key)
    }

    override fun putLong(key: String, value: Long) {
        settings.putLong(key, value)
        notifyListeners(key)
    }

    override fun putString(key: String, value: String) {
        settings.putString(key, encrypt(value))
        notifyListeners(key)
    }

    override fun remove(key: String) {
        settings.remove(key)
        notifyListeners(key)
    }

    override val keys: Set<String>
        get() = settings.keys

    override val size: Int
        get() = settings.size

    private fun decrypt(value: String): String {
        if (value.isEmpty()) return ""
        val bytes = encryptionHelper.decodeFromString(value)
        return encryptionHelper.decrypt(bytes).orEmpty()
    }

    private fun encrypt(value: String): String {
        val bytes = encryptionHelper.encrypt(value) ?: byteArrayOf()
        return encryptionHelper.encodeToString(bytes)
    }

    private fun addGenericListener(key: String, onChange: () -> Unit): SettingsListener {
        var prevValue = getRawValue(key)

        val checkAndNotify = {
            val currentValue = getRawValue(key)
            if (prevValue != currentValue) {
                prevValue = currentValue
                onChange()
            }
        }

        val delegateListener = (settings as? ObservableSettings)?.addBooleanListener(key, false) {
            checkAndNotify()
        } ?: (settings as? ObservableSettings)?.addStringOrNullListener(key) {
            checkAndNotify()
        }

        val callbackList = listeners.getOrPut(key) { mutableListOf() }
        callbackList.add(checkAndNotify)

        return CustomSettingsListener {
            delegateListener?.deactivate()
            listeners[key]?.remove(checkAndNotify)
        }
    }

    private fun notifyListeners(key: String) {
        listeners[key]?.toList()?.forEach { callback ->
            callback()
        }
    }

    private fun getRawValue(key: String): Any? {
        if (!settings.hasKey(key)) return null
        return settings.getStringOrNull(key)
            ?: settings.getBooleanOrNull(key)
            ?: settings.getIntOrNull(key)
            ?: settings.getLongOrNull(key)
            ?: settings.getFloatOrNull(key)
            ?: settings.getDoubleOrNull(key)
    }

    private fun interface CustomSettingsListener : SettingsListener {
        override fun deactivate()
    }
}
