package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultFollowedHashtagCache(private val tagRepository: TagRepository) : FollowedHashtagCache {
    private var cache: List<TagModel> = emptyList()
    private val mutex = Mutex()

    override suspend fun getAll(): List<TagModel> = mutex.withLock { cache }

    override suspend fun refresh() {
        mutex.withLock {
            var cursor: String? = null
            var canFetchMore = true
            var iter = 0
            while (canFetchMore && iter < MAX_ITERATIONS) {
                tagRepository.getFollowed(cursor)?.also { res ->
                    cache += res.list
                    cursor = res.cursor
                    canFetchMore = res.list.isNotEmpty()
                    iter++
                }
            }
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            cache = emptyList()
        }
    }

    override suspend fun isFollowed(tag: TagModel): Boolean = mutex.withLock {
        cache.any { it.name == tag.name }
    }

    companion object {
        private const val MAX_ITERATIONS = 20
    }
}
