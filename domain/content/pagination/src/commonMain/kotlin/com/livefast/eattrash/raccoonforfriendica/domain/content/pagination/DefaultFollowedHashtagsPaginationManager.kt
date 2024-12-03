package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
internal class DefaultFollowedHashtagsPaginationManager(
    private val tagRepository: TagRepository,
) : FollowedHashtagsPaginationManager {
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<TagModel>()
    private val mutex = Mutex()

    override suspend fun reset() {
        pageCursor = null
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TagModel> {
        val results = tagRepository.getFollowed(pageCursor)

        return mutex.withLock {
            results
                ?.updatePaginationData()
                ?.deduplicate()
                ?.also { history.addAll(it) }
            // return a copy
            history.map { it }
        }
    }

    private fun ListWithPageCursor<TagModel>.updatePaginationData(): List<TagModel> =
        run {
            pageCursor = cursor
            canFetchMore = list.isNotEmpty()
            list
        }

    private fun List<TagModel>.deduplicate(): List<TagModel> =
        filter { e1 ->
            history.none { e2 -> e1.name == e2.name }
        }.distinctBy { it.name }
}
