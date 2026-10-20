package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultReplyHelper(
    private val postRepository: PostRepository,
    private val postCache: LruCache<String, PostModel> = LruCache.factory(100),
) : ReplyHelper {
    override suspend fun PostModel.withInReplyToIfMissing(): PostModel {
        val parent = inReplyTo ?: return this
        if (parent.content.isNotEmpty()) {
            return this
        }

        val parentId = parent.id
        val cachedValue = postCache.get(parentId)
        if (cachedValue != null) {
            return copy(inReplyTo = cachedValue)
        }

        val remoteParent =
            postRepository
                .getById(parentId)
                ?.also { postCache.put(parentId, it) }
        return copy(inReplyTo = remoteParent)
    }
}
