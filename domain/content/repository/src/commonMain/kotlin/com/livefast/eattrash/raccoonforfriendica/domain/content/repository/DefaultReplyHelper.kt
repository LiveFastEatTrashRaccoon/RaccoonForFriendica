package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel

internal class DefaultReplyHelper(
    private val entryRepository: TimelineEntryRepository,
    private val entryCache: LruCache<String, TimelineEntryModel> = LruCache.factory(100),
) : ReplyHelper {
    override suspend fun TimelineEntryModel.withInReplyToIfMissing(): TimelineEntryModel {
        val parent = inReplyTo ?: return this
        check(parent.content.isEmpty()) { return this }

        val parentId = parent.id
        val cachedValue = entryCache.get(parentId)
        check(cachedValue == null) { return copy(inReplyTo = cachedValue) }

        val remoteParent =
            entryRepository
                .getById(parentId)
                ?.also { entryCache.put(parentId, it) }
        return copy(inReplyTo = remoteParent)
    }
}
