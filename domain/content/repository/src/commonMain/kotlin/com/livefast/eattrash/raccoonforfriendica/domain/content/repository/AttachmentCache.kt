package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

interface AttachmentCache {
    fun put(bytes: ByteArray)

    fun get(): ByteArray?

    fun clear()
}
