package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import org.kodein.di.instance

fun getAttachmentCache(): AttachmentCache {
    val res by RootDI.di.instance<AttachmentCache>()
    return res
}

fun getEntryCache(): LocalItemCache<TimelineEntryModel> {
    val res by RootDI.di.instance<LocalItemCache<TimelineEntryModel>>()
    return res
}
