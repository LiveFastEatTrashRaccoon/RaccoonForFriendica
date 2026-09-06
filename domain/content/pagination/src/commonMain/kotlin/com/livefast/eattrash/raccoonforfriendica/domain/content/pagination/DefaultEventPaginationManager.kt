package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EventRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(scope = AppScope::class, binding = binding<EventPaginationManager>())
@Inject
class DefaultEventPaginationManager(private val eventRepository: EventRepository) :
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
