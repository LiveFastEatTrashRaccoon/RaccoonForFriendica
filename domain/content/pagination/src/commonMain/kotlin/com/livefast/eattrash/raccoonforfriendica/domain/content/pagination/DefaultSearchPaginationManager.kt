package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultSearchPaginationManager(
    private val searchRepository: SearchRepository,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SearchPaginationManager {
    private var specification: SearchPaginationSpecification? = null
    override var canFetchMore: Boolean = true
    private var pageCursor: String? = null
    private val history = mutableListOf<ExploreItemModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(UserUpdatedEvent::class)
            .onEach { event ->
                mutex.withLock {
                    val idx =
                        history.indexOfFirst { e -> e is ExploreItemModel.User && e.user.id == event.user.id }
                    if (idx >= 0) {
                        (history[idx] as? ExploreItemModel.User)?.copy(user = event.user)?.also {
                            history[idx] = it
                        }
                    }
                }
            }.launchIn(scope)
    }

    override suspend fun reset(specification: SearchPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<ExploreItemModel> {
        val specification = this.specification ?: return emptyList()
        val results =
            when (specification) {
                is SearchPaginationSpecification.Entries ->
                    searchRepository
                        .search(
                            query = specification.query,
                            pageCursor = pageCursor,
                            type = SearchResultType.Entries,
                        ).filterNsfw(specification.includeNsfw)

                is SearchPaginationSpecification.Hashtags ->
                    searchRepository
                        .search(
                            query = specification.query,
                            pageCursor = pageCursor,
                            type = SearchResultType.Hashtags,
                        )

                is SearchPaginationSpecification.Users ->
                    searchRepository
                        .search(
                            query = specification.query,
                            pageCursor = pageCursor,
                            type = SearchResultType.Users,
                        )
            }.deduplicate().updatePaginationData()
        mutex.withLock {
            history.addAll(results)
        }

        // return an object containing copies
        return history.map { it }
    }

    private fun List<ExploreItemModel>.updatePaginationData(): List<ExploreItemModel> =
        apply {
            lastOrNull()?.also {
                pageCursor = it.id
            }
            canFetchMore = isNotEmpty()
        }

    private fun List<ExploreItemModel>.deduplicate(): List<ExploreItemModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }

    private fun List<ExploreItemModel>.filterNsfw(included: Boolean): List<ExploreItemModel> = filter { included || !it.isNsfw }
}
