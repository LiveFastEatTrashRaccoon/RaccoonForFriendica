package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import org.koin.core.annotation.Single

@Single
internal class DefaultReplyHelper(
    private val entryRepository: TimelineEntryRepository,
    private val entryCache: LruCache<String, TimelineEntryModel> = LruCache(100),
) : ReplyHelper {
    override suspend fun TimelineEntryModel.withInReplyToIfMissing(): TimelineEntryModel {
        val parent = inReplyTo ?: return this
        if (parent.content.isNotEmpty()) {
            return this
        }

        val parentId = parent.id
        val cachedValue = entryCache.get(parentId)
        if (cachedValue != null) {
            return cachedValue
        }

        val remoteParent =
            entryRepository
                .getById(parentId)
                ?.also { entryCache.put(parentId, it) }
        return copy(inReplyTo = remoteParent)
    }
}
