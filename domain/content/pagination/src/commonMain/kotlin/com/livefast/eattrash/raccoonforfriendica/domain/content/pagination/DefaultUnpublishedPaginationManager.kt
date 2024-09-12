package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository

internal class DefaultUnpublishedPaginationManager(
    private val scheduledEntryRepository: ScheduledEntryRepository,
) : UnpublishedPaginationManager {
    private var specification: UnpublishedPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    override val history = mutableListOf<TimelineEntryModel>()

    override suspend fun reset(specification: UnpublishedPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is UnpublishedPaginationSpecification.Scheduled -> {
                    when (specification) {
                        UnpublishedPaginationSpecification.Scheduled ->
                            scheduledEntryRepository.getAll(pageCursor)
                        else -> emptyList()
                    }
                }
            }.updatePaginationData().deduplicate()

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
        }.distinctBy { it.id }
}
