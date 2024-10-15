package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultFavoritesPaginationManager(
    private val timelineEntryRepository: TimelineEntryRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FavoritesPaginationManager {
    private var specification: FavoritesPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<TimelineEntryModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(TimelineEntryUpdatedEvent::class)
            .onEach { event ->
                val idx = history.indexOfFirst { e -> e.id == event.entry.id }
                if (idx >= 0) {
                    history[idx] = event.entry
                }
            }.launchIn(scope)
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
    }

    override suspend fun reset(specification: FavoritesPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is FavoritesPaginationSpecification.Bookmarks ->
                    timelineEntryRepository
                        .getBookmarks(pageCursor = pageCursor)
                        ?.filterNsfw(specification.includeNsfw)

                is FavoritesPaginationSpecification.Favorites ->
                    timelineEntryRepository
                        .getFavorites(pageCursor = pageCursor)
                        ?.filterNsfw(specification.includeNsfw)
            }?.deduplicate()
                ?.updatePaginationData()
                ?.fixupCreatorEmojis()
                ?.fixupInReplyTo()
                .orEmpty()
        mutex.withLock {
            history.addAll(results)
        }

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

    private fun List<TimelineEntryModel>.filterNsfw(included: Boolean): List<TimelineEntryModel> = filter { included || !it.isNsfw }

    private suspend fun List<TimelineEntryModel>.fixupCreatorEmojis(): List<TimelineEntryModel> =
        with(emojiHelper) {
            map {
                it.withEmojisIfMissing()
            }
        }

    private suspend fun List<TimelineEntryModel>.fixupInReplyTo(): List<TimelineEntryModel> =
        with(replyHelper) {
            map {
                it.withInReplyToIfMissing()
            }
        }
}
