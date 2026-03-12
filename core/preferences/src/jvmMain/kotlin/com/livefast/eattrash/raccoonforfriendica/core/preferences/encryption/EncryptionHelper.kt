package com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption

interface EncryptionHelper {

    fun encrypt(input: String): ByteArray?

    fun decrypt(input: ByteArray): String?

    fun encodeToString(input: ByteArray): String

    fun decodeFromString(input: String): ByteArray
}
