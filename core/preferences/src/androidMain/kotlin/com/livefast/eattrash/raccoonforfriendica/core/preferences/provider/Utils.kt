package com.livefast.eattrash.raccoonforfriendica.core.preferences.provider

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption.EncryptionHelper

private const val KEY_MIGRATED = "migrated"

@SuppressLint("UseKtx")
internal fun migrate(from: SharedPreferences, to: SharedPreferences, encryptionHelper: EncryptionHelper) {
    val migrated = to.getBoolean(KEY_MIGRATED, false)
    if (!migrated) {
        val entries = from.all.entries
        for (entry in entries) {
            val key = entry.key
            when (entry.value::class.java) {
                String::class.java -> {
                    val value = entry.value as String
                    val encryptedValue = encryptionHelper.encrypt(value) ?: byteArrayOf()
                    val valueToWrite = encryptionHelper.encodeToString(encryptedValue)
                    to.edit().putString(key, valueToWrite).apply()
                }

                Boolean::class.java -> {
                    val value = entry.value as Boolean
                    to.edit().putBoolean(key, value).apply()
                }

                Int::class.java -> {
                    val value = entry.value as Int
                    to.edit().putInt(key, value).apply()
                }

                Long::class.java -> {
                    val value = entry.value as Long
                    to.edit().putLong(key, value).apply()
                }

                Float::class.java -> {
                    val value = entry.value as Float
                    to.edit().putFloat(key, value).apply()
                }

                Double::class.java -> {
                    val value = entry.value as Double
                    to.edit().putLong(key, value.toRawBits()).apply()
                }

                else -> Unit
            }
        }

        to.edit().putBoolean(KEY_MIGRATED, true).apply()
    }
}
