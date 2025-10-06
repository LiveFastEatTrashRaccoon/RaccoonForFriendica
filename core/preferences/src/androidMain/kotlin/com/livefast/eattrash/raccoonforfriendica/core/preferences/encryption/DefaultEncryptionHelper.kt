package com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.io.encoding.Base64

internal class DefaultEncryptionHelper : EncryptionHelper {

    private val store by lazy {
        KeyStore.getInstance(STORE_NAME).apply { load(null) }
    }

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
        require(input.size > IV_SIZE) { return null }

        val iv = input.sliceArray(0 until IV_SIZE)
        val data = input.sliceArray(IV_SIZE until input.size)
        val cipher = Cipher.getInstance(CIPHER)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(data).toString(Charsets.UTF_8)
    }

    override fun encodeToString(input: ByteArray): String = Base64.encode(input)

    override fun decodeFromString(input: String): ByteArray = Base64.decode(source = input)

    private fun getOrCreateMasterKey(): SecretKey? {
        val existing = store.containsAlias(ALIAS_NAME)
        return if (!existing) {
            createMasterKey()
        } else {
            retrieveMasterKey()
        }
    }

    private fun createMasterKey(): SecretKey {
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME)
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val params = KeyGenParameterSpec.Builder(ALIAS_NAME, purposes).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()
        generator.init(params)
        return generator.generateKey()
    }

    private fun retrieveMasterKey(): SecretKey? =
        (store.getEntry(ALIAS_NAME, null) as? KeyStore.SecretKeyEntry)?.secretKey

    companion object {
        private const val STORE_NAME = "AndroidKeyStore"
        private const val ALIAS_NAME = "r4f"
        private const val CIPHER = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
    }
}
