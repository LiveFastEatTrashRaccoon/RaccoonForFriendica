package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory

@Factory
internal class DefaultUnpublishedPaginationManager(
    private val scheduledEntryRepository: ScheduledEntryRepository,
    private val draftRepository: DraftRepository,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<TimelineEntryModel, UnpublishedPaginationSpecification>(
    idSelector = { it.id },
),
    UnpublishedPaginationManager {
    private var page: Int = 0
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(TimelineEntryDeletedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    removeHistoryItem(event.id)
                }
            }.launchIn(scope)
        notificationCenter
            .subscribe(TimelineEntryUpdatedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    updateHistoryItem(event.entry.id) { event.entry }
                }
            }.launchIn(scope)
    }

    override suspend fun reset(specification: UnpublishedPaginationSpecification) {
        super.reset(specification)
        withPaginationLock {
            page = 0
        }
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val spec = currentSpecification ?: return emptyList()

        val results =
            when (spec) {
                UnpublishedPaginationSpecification.Scheduled ->
                    scheduledEntryRepository.getAll(currentPageCursor)

                UnpublishedPaginationSpecification.Drafts ->
                    draftRepository.getAll(page = page)
            }

        return updateHistory(
            items = results,
            transform = { newItems ->
                if (newItems.isNotEmpty()) {
                    page++
                }
                newItems
            },
        )
    }
}
