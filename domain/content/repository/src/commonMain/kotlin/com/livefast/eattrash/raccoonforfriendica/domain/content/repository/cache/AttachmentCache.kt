package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache

interface AttachmentCache {
    fun put(bytes: ByteArray)

    fun get(): ByteArray?

    fun clear()
}
