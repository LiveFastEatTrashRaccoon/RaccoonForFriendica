package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository

internal class DefaultFavoritesPaginationManager(
    private val timelineEntryRepository: TimelineEntryRepository,
) : FavoritesPaginationManager {
    private var specification: FavoritesPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<TimelineEntryModel>()

    override suspend fun reset(specification: FavoritesPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                FavoritesPaginationSpecification.Bookmarks -> {
                    timelineEntryRepository.getBookmarks(pageCursor = pageCursor)
                }

                FavoritesPaginationSpecification.Favorites -> {
                    timelineEntryRepository.getFavorites(pageCursor = pageCursor)
                }
            }.deduplicate()

        if (results.isNotEmpty()) {
            pageCursor = results.last().id
            canFetchMore = true
        } else {
            canFetchMore = false
        }

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }
}
