package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository

internal class DefaultFollowedHashtagsPaginationManager(
    private val tagRepository: TagRepository,
) : FollowedHashtagsPaginationManager {
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<TagModel>()

    override suspend fun reset() {
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TagModel> {
        val results =
            tagRepository
                .getFollowed(pageCursor)
                ?.let { (tags, cursor) ->
                    tags.deduplicate().updatePaginationData(cursor)
                }.orEmpty()

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<TagModel>.updatePaginationData(cursor: String?): List<TagModel> =
        apply {
            pageCursor = cursor
            canFetchMore = isNotEmpty()
        }

    private fun List<TagModel>.deduplicate(): List<TagModel> =
        filter { e1 ->
            history.none { e2 -> e1.name == e2.name }
        }.distinctBy { it.name }
}
