package com.livefast.eattrash.raccoonforfriendica.core.encryption

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class DefaultSecureKeyStorage(private val context: Context) : SecureKeyStorage {

    private val store by lazy {
        KeyStore.getInstance(STORE_NAME).apply { load(null) }
    }

    private val preferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun storeKey(alias: String, key: ByteArray) {
        val secretKey = getOrCreateMasterKey(alias) ?: return
        val cipher = Cipher.getInstance(CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val data = cipher.doFinal(key)
        val iv = cipher.iv

        writeToPrefs(input = iv + data, alias = alias)
    }

    override fun getKey(alias: String): ByteArray? {
        val secretKey = getOrCreateMasterKey(alias) ?: return null
        val input = readFromPrefs(alias) ?: return null

        val iv = input.sliceArray(0 until IV_SIZE)
        val data = input.sliceArray(IV_SIZE until input.size)
        val cipher = Cipher.getInstance(CIPHER)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(data)
    }

    private fun getOrCreateMasterKey(alias: String): SecretKey? {
        val existing = store.containsAlias(alias)
        return if (!existing) {
            createMasterKey(alias)
        } else {
            retrieveMasterKey(alias)
        }
    }

    private fun createMasterKey(alias: String): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME)
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val params = KeyGenParameterSpec.Builder(alias, purposes)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        generator.init(params)
        return generator.generateKey()
    }

    private fun retrieveMasterKey(alias: String): SecretKey? =
        (store.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.secretKey

    internal fun reset() {
        preferences.edit { clear() }
    }

    private fun readFromPrefs(alias: String): ByteArray? {
        val rawValue = preferences.getString(PREFS_ENTRY_PREFIX + alias, "")?.takeIf { it.isNotEmpty() } ?: return null
        val serialized = rawValue.toByteArray(Charsets.UTF_8)
        return Base64.decode(serialized, Base64.DEFAULT)
    }

    private fun writeToPrefs(input: ByteArray, alias: String) {
        preferences.edit {
            val rawValue = Base64.encode(input, Base64.DEFAULT)
            val serialized = rawValue.toString(Charsets.UTF_8)
            putString(PREFS_ENTRY_PREFIX + alias, serialized)
        }
    }

    companion object {
        private const val STORE_NAME = "AndroidKeyStore"
        private const val CIPHER = "AES/GCM/NoPadding"
        private const val PREFS_NAME = "DefaultSecureKeyStoragePrefs"
        private const val PREFS_ENTRY_PREFIX = "entry_"
        private const val IV_SIZE = 12
    }
}
