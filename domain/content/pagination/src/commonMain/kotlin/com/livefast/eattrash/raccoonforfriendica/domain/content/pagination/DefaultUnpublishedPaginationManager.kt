package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledPostRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ContributesBinding(scope = AppScope::class, binding = binding<UnpublishedPaginationManager>())
@Inject
class DefaultUnpublishedPaginationManager(
    private val scheduledPostRepository: ScheduledPostRepository,
    private val draftRepository: DraftRepository,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<PostModel, UnpublishedPaginationSpecification>(
    idSelector = { it.id },
),
    UnpublishedPaginationManager {
    private var page: Int = 0
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(PostDeletedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    removeHistoryItem(event.id)
                }
            }.launchIn(scope)
        notificationCenter
            .subscribe(PostUpdatedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    updateHistoryItem(event.post.id) { event.post }
                }
            }.launchIn(scope)
    }

    override suspend fun reset(specification: UnpublishedPaginationSpecification) {
        super.reset(specification)
        withPaginationLock {
            page = 0
        }
    }

    override suspend fun loadNextPage(): List<PostModel> {
        val spec = currentSpecification ?: return emptyList()

        val results =
            when (spec) {
                UnpublishedPaginationSpecification.Scheduled ->
                    scheduledPostRepository.getAll(currentPageCursor)

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
