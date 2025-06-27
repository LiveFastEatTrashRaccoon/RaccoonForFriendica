package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

internal class DefaultAttachmentCache : AttachmentCache {
    private var value: ByteArray? = null

    override fun put(bytes: ByteArray) {
        value = bytes
    }

    override fun get(): ByteArray? = value

    override fun clear() {
        value = null
    }
}
