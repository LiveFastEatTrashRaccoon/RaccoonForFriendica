package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository

internal class DefaultDirectMessagesPaginationManager(
    private val directMessageRepository: DirectMessageRepository,
) : DirectMessagesPaginationManager {
    private var specification: DirectMessagesPaginationSpecification? = null
    private var page = 1
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<DirectMessageModel>()

    override suspend fun reset(specification: DirectMessagesPaginationSpecification) {
        this.specification = specification
        page = 1
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<DirectMessageModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                DirectMessagesPaginationSpecification.All ->
                    directMessageRepository
                        .getAll(
                            page = page,
                            limit = 40,
                        )?.deduplicate()
                        ?.updatePaginationData()
                        .orEmpty()

                is DirectMessagesPaginationSpecification.Replies ->
                    directMessageRepository
                        .getReplies(specification.parentUri)
                        ?.deduplicate()
                        ?.updatePaginationData()
                        .orEmpty()
            }
        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<DirectMessageModel>.updatePaginationData(): List<DirectMessageModel> =
        apply {
            val hasData = isNotEmpty()
            if (hasData) {
                page++
            }
            canFetchMore = hasData
        }

    private fun List<DirectMessageModel>.deduplicate(): List<DirectMessageModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }
}
