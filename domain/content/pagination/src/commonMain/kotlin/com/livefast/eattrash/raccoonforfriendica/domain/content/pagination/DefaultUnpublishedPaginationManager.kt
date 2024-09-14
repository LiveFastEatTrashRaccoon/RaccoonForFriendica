package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository

internal class DefaultUnpublishedPaginationManager(
    private val scheduledEntryRepository: ScheduledEntryRepository,
    private val draftRepository: DraftRepository,
) : UnpublishedPaginationManager {
    private var specification: UnpublishedPaginationSpecification? = null
    private var pageCursor: String? = null
    private var page: Int = 0
    override var canFetchMore: Boolean = true
    override val history = mutableListOf<TimelineEntryModel>()

    override suspend fun reset(specification: UnpublishedPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        page = 0
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                UnpublishedPaginationSpecification.Scheduled ->
                    scheduledEntryRepository.getAll(pageCursor)

                UnpublishedPaginationSpecification.Drafts ->
                    draftRepository.getAll(page = page)
            }?.updatePaginationData()
                ?.deduplicate()
                .orEmpty()

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<TimelineEntryModel>.updatePaginationData(): List<TimelineEntryModel> =
        apply {
            lastOrNull()?.also {
                pageCursor = it.id
            }
            if (isNotEmpty()) {
                page++
            }
            canFetchMore = isNotEmpty()
        }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }
}
