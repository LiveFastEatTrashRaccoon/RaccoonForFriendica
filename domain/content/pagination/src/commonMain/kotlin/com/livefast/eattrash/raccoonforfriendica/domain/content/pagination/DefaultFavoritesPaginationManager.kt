package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
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
                is FavoritesPaginationSpecification.Bookmarks ->
                    timelineEntryRepository
                        .getBookmarks(pageCursor = pageCursor)
                        .updatePaginationData()
                        .filterNsfw(specification.includeNsfw)

                is FavoritesPaginationSpecification.Favorites ->
                    timelineEntryRepository
                        .getFavorites(pageCursor = pageCursor)
                        .updatePaginationData()
                        .filterNsfw(specification.includeNsfw)
            }.deduplicate()
        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<TimelineEntryModel>.updatePaginationData(): List<TimelineEntryModel> =
        apply {
            lastOrNull()?.also {
                pageCursor = it.id
            }
            canFetchMore = isNotEmpty()
        }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }

    private fun List<TimelineEntryModel>.filterNsfw(included: Boolean): List<TimelineEntryModel> = filter { included || !it.isNsfw }
}
