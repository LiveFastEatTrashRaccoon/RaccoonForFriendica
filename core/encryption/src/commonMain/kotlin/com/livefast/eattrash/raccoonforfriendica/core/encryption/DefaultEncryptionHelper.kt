package com.livefast.eattrash.raccoonforfriendica.core.encryption

import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class DefaultEncryptionHelper(
    private val storage: SecureKeyStorage,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : EncryptionHelper {

    private val aesGcm = CryptographyProvider.Default.get(AES.GCM)

    private lateinit var cipher: AES.IvAuthenticatedCipher
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val initializationJob = scope.launch {
        val existingKey = getExistingKey()
        val key = existingKey ?: createKey().also { storeKey(it) }
        cipher = key.cipher(tagSize = 128.bits)
    }

    override suspend fun encrypt(input: String): ByteArray {
        initializationJob.join()
        return cipher.encrypt(plaintext = input.encodeToByteArray())
    }

    override suspend fun decrypt(input: ByteArray): String {
        initializationJob.join()
        return cipher.decrypt(ciphertext = input).decodeToString()
    }

    private suspend fun createKey(): AES.GCM.Key {
        val generator = aesGcm.keyGenerator(keySize = AES.Key.Size.B256)
        return generator.generateKey()
    }

    private suspend fun getExistingKey(): AES.GCM.Key? {
        val encodedKey = storage.getKey(ALIAS) ?: return null
        val decoder = aesGcm.keyDecoder()
        return decoder.decodeFromByteArray(AES.Key.Format.RAW, encodedKey)
    }

    private suspend fun storeKey(key: AES.GCM.Key) {
        val encodedKey = key.encodeToByteArray(AES.Key.Format.RAW)
        storage.storeKey(alias = ALIAS, encodedKey)
    }

    companion object {
        private const val ALIAS = "rff_alias"
    }
}
