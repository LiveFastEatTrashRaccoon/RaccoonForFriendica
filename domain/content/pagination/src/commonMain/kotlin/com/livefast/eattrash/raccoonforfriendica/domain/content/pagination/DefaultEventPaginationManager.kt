package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EventRepository
import org.koin.core.annotation.Factory

@Factory
internal class DefaultEventPaginationManager(private val eventRepository: EventRepository) :
    BasePaginationManager<EventModel, EventsPaginationSpecification>(
        idSelector = { it.id },
    ),
    EventPaginationManager {

    override suspend fun loadNextPage(): List<EventModel> {
        val spec = currentSpecification ?: return emptyList()
        val results =
            when (spec) {
                EventsPaginationSpecification.All ->
                    eventRepository.getAll(
                        pageCursor = currentPageCursor,
                    )
            }

        return updateHistory(
            items = results,
        )
    }
}
