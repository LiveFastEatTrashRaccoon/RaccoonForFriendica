package com.livefast.eattrash.raccoonforfriendica.core.encryption

interface EncryptionHelper {
    suspend fun encrypt(input: String): ByteArray

    suspend fun decrypt(input: ByteArray): String
}
