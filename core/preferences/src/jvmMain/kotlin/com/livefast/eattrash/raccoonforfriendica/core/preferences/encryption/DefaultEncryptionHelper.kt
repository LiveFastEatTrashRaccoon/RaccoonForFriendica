package com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption

import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class DefaultEncryptionHelper : EncryptionHelper {

    override fun encrypt(input: String): ByteArray? {
        val secretKey = getOrCreateMasterKey() ?: return null
        val cipher = Cipher.getInstance(CIPHER)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val data = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
        val iv = cipher.iv

        return iv + data
    }

    override fun decrypt(input: ByteArray): String? {
        val secretKey = getOrCreateMasterKey() ?: return null
        if (input.size <= IV_SIZE) return null

        val iv = input.sliceArray(0 until IV_SIZE)
        val data = input.sliceArray(IV_SIZE until input.size)
        val cipher = Cipher.getInstance(CIPHER)
        val spec = GCMParameterSpec(T_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(data).toString(Charsets.UTF_8)
    }

    override fun encodeToString(input: ByteArray): String = Base64.getEncoder().encodeToString(input)

    override fun decodeFromString(input: String): ByteArray = Base64.getMimeDecoder().decode(input)

    private fun getOrCreateMasterKey(): SecretKey? = synchronized(store) {
        val existing = store.containsAlias(ALIAS_NAME)
        return if (!existing) {
            createMasterKey()
        } else {
            retrieveMasterKey()
        }
    }

    private fun createMasterKey(): SecretKey {
        val generator = KeyGenerator.getInstance(ALGORITHM)
        generator.init(KEY_SIZE)
        val key = generator.generateKey()
        val protection = KeyStore.PasswordProtection(KEY_PASSWORD.toCharArray())
        store.setEntry(ALIAS_NAME, KeyStore.SecretKeyEntry(key), protection)
        saveStore()
        return key
    }

    private fun retrieveMasterKey(): SecretKey? {
        val protection = KeyStore.PasswordProtection(KEY_PASSWORD.toCharArray())
        return (store.getEntry(ALIAS_NAME, protection) as? KeyStore.SecretKeyEntry)?.secretKey
    }

    private fun saveStore() {
        val file = java.io.File(KEYSTORE_PATH)
        file.parentFile?.mkdirs()
        file.outputStream().use {
            store.store(it, STORE_PASSWORD.toCharArray())
        }
    }

    companion object {
        private val STORE_NAME = KeyStore.getDefaultType()
        private val KEYSTORE_PATH = System.getProperty("user.home") + "/.r4f_ks.p12"
        private const val ALGORITHM = "AES"
        private const val ALIAS_NAME = "r4f"
        private const val CIPHER = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val IV_SIZE = 12
        private const val T_LENGTH = 128
        private const val STORE_PASSWORD = "ks-r4f"
        private const val KEY_PASSWORD = "r4f"

        private val store by lazy {
            val ks = KeyStore.getInstance(STORE_NAME)
            val file = java.io.File(KEYSTORE_PATH)
            if (file.exists()) {
                file.inputStream().use { ks.load(it, STORE_PASSWORD.toCharArray()) }
            } else {
                ks.load(null, STORE_PASSWORD.toCharArray())
            }
            ks
        }
    }
}
