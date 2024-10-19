package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EventRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultEventPaginationManager(
    private val eventRepository: EventRepository,
) : EventPaginationManager {
    private var specification: EventsPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<EventModel>()
    private val mutex = Mutex()

    override suspend fun reset(specification: EventsPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<EventModel> {
        val specification = this.specification ?: return emptyList()
        val results =
            when (specification) {
                EventsPaginationSpecification.All ->
                    eventRepository.getAll(
                        pageCursor = pageCursor,
                    )
            }?.updatePaginationData()
                ?.deduplicate()
                .orEmpty()

        mutex.withLock {
            history.addAll(results)
        }

        // return a copy
        return history.map { it }
    }

    private fun List<EventModel>.updatePaginationData(): List<EventModel> =
        apply {
            lastOrNull()?.also {
                pageCursor = it.id
            }
            canFetchMore = isNotEmpty()
        }

    private fun List<EventModel>.deduplicate(): List<EventModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }
}
