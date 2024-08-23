package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository

internal class DefaultExplorePaginationManager(
    private val trendingRepository: TrendingRepository,
    private val userRepository: UserRepository,
) : ExplorePaginationManager {
    private var specification: ExplorePaginationSpecification? = null
    private var offset = 0
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<ExploreItemModel>()

    override suspend fun reset(specification: ExplorePaginationSpecification) {
        this.specification = specification
        offset = 0
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<ExploreItemModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                ExplorePaginationSpecification.Hashtags ->
                    trendingRepository.getHashtags(offset).map {
                        ExploreItemModel.HashTag(it)
                    }

                ExplorePaginationSpecification.Links ->
                    trendingRepository.getLinks(offset).mapNotNull {
                        if (it.url.isBlank()) {
                            null
                        } else {
                            ExploreItemModel.Link(it)
                        }
                    }

                is ExplorePaginationSpecification.Posts ->
                    trendingRepository
                        .getEntries(offset)
                        .map {
                            ExploreItemModel.Entry(it)
                        }.filterNsfw(specification.includeNsfw)

                ExplorePaginationSpecification.Suggestions ->
                    userRepository.getSuggestions().map {
                        ExploreItemModel.User(
                            it.copy(
                                relationshipStatus = RelationshipStatus.Undetermined,
                            ),
                        )
                    }
            }.deduplicate().updatePaginationData()
        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<ExploreItemModel>.updatePaginationData(): List<ExploreItemModel> =
        apply {
            offset = size
            canFetchMore = isNotEmpty()
        }

    private fun List<ExploreItemModel>.deduplicate(): List<ExploreItemModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }

    private fun List<ExploreItemModel>.filterNsfw(included: Boolean): List<ExploreItemModel> = filter { included || !it.isNsfw }
}
