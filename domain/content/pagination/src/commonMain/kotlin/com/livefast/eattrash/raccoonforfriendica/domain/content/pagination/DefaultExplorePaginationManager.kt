package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendsRepository

internal class DefaultExplorePaginationManager(
    private val trendsRepository: TrendsRepository,
    private val accountRepository: AccountRepository,
) : ExplorePaginationManager {
    private var specification: ExplorePaginationSpecification? = null
    private var offset = 0
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<ExploreItemModel>()

    override suspend fun reset(specification: ExplorePaginationSpecification) {
        this.specification = specification
        offset = 0
        history.clear()
    }

    override suspend fun loadNextPage(): List<ExploreItemModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                ExplorePaginationSpecification.Hashtags ->
                    trendsRepository.getHashtags(offset).map {
                        ExploreItemModel.HashTag(it)
                    }

                ExplorePaginationSpecification.Links ->
                    trendsRepository.getLinks(offset).map {
                        ExploreItemModel.Link(it)
                    }

                ExplorePaginationSpecification.Posts ->
                    trendsRepository.getEntries(offset).map {
                        ExploreItemModel.Entry(it)
                    }

                ExplorePaginationSpecification.Suggestions ->
                    accountRepository.getSuggestions().map {
                        ExploreItemModel.Suggestion(
                            it.copy(
                                relationshipStatus = RelationshipStatus.Undetermined,
                            ),
                        )
                    }
            }.deduplicate()

        if (results.isNotEmpty()) {
            offset = results.size
            canFetchMore = true
        } else {
            canFetchMore = false
        }

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<ExploreItemModel>.deduplicate(): List<ExploreItemModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }
}
