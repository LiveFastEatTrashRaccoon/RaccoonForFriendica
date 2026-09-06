package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultAttachmentCache : AttachmentCache {
    private var value: ByteArray? = null

    override fun put(bytes: ByteArray) {
        value = bytes
    }

    override fun get(): ByteArray? = value

    override fun clear() {
        value = null
    }
}
