package com.livefast.eattrash.raccoonforfriendica.core.encryption

interface SecureKeyStorage {
    fun storeKey(alias: String, key: ByteArray)

    fun getKey(alias: String): ByteArray?
}
