package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import org.koin.core.qualifier.TypeQualifier

fun getAttachmentCache(): AttachmentCache = getByInjection(AttachmentCache::class)

@Suppress("UNCHECKED_CAST")
fun getEntryCache(): LocalItemCache<TimelineEntryModel> {
    val qualifier = TypeQualifier(TimelineEntryModel::class)
    val res: LocalItemCache<TimelineEntryModel> = getByInjection(
        LocalItemCache::class,
        qualifier,
    ) as LocalItemCache<TimelineEntryModel>
    return res
}
