package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AttachmentCache
import org.kodein.di.instance

fun getAttachmentCache(): AttachmentCache {
    val res by RootDI.di.instance<AttachmentCache>()
    return res
}
