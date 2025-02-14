package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultUnpublishedPaginationManager(
    private val scheduledEntryRepository: ScheduledEntryRepository,
    private val draftRepository: DraftRepository,
    notificationCenter: NotificationCenter = getNotificationCenter(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UnpublishedPaginationManager {
    private var specification: UnpublishedPaginationSpecification? = null
    private var pageCursor: String? = null
    private var page: Int = 0
    override var canFetchMore: Boolean = true
    override val history = mutableListOf<TimelineEntryModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(TimelineEntryDeletedEvent::class)
            .onEach { event ->
                mutex.withLock {
                    val idx = history.indexOfFirst { e -> e.id == event.id }
                    if (idx >= 0) {
                        history.removeAt(idx)
                    }
                }
            }.launchIn(scope)
        notificationCenter
            .subscribe(TimelineEntryUpdatedEvent::class)
            .onEach { event ->
                mutex.withLock {
                    val idx = history.indexOfFirst { e -> e.id == event.entry.id }
                    if (idx >= 0) {
                        history[idx] = event.entry
                    }
                }
            }.launchIn(scope)
    }

    override suspend fun reset(specification: UnpublishedPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        page = 0
        mutex.withLock {
            history.clear()
        }
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
            }.orEmpty()

        return mutex.withLock {
            results
                .deduplicate()
                .updatePaginationData()
                .also { history.addAll(it) }
            // return a copy
            history.map { it }
        }
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
