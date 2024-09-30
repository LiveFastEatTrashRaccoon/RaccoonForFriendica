package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultTimelinePaginationManager(
    private val timelineRepository: TimelineRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val emojiHelper: EmojiHelper,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TimelinePaginationManager {
    private var specification: TimelinePaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    override val history = mutableListOf<TimelineEntryModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
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

    override suspend fun reset(specification: TimelinePaginationSpecification) {
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
                is TimelinePaginationSpecification.Feed -> {
                    when (specification.timelineType) {
                        TimelineType.All ->
                            timelineRepository.getPublic(
                                pageCursor = pageCursor,
                            )

                        TimelineType.Subscriptions ->
                            timelineRepository.getHome(
                                pageCursor = pageCursor,
                            )

                        TimelineType.Local ->
                            timelineRepository.getLocal(
                                pageCursor = pageCursor,
                            )

                        is TimelineType.Circle ->
                            timelineRepository.getCircle(
                                id = specification.timelineType.id,
                                pageCursor = pageCursor,
                            )
                    }?.updatePaginationData()
                        ?.filterReplies(included = !specification.excludeReplies)
                        ?.filterNsfw(specification.includeNsfw)
                        ?.deduplicate()
                        ?.fixupCreatorEmojis()
                        .orEmpty()
                }

                is TimelinePaginationSpecification.Hashtag -> {
                    timelineRepository
                        .getHashtag(
                            hashtag = specification.hashtag,
                            pageCursor = pageCursor,
                        )?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.deduplicate()
                        ?.fixupCreatorEmojis()
                        .orEmpty()
                }

                is TimelinePaginationSpecification.User ->
                    timelineEntryRepository
                        .getByUser(
                            userId = specification.userId,
                            pageCursor = pageCursor,
                            excludeReplies = specification.excludeReplies,
                            excludeReblogs = specification.excludeReblogs,
                            onlyMedia = specification.onlyMedia,
                            pinned = specification.pinned,
                        )
                        // intended: there is a bug in user pagination
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.fixupCreatorEmojis()
                        .orEmpty()

                is TimelinePaginationSpecification.Forum ->
                    timelineEntryRepository
                        .getByUser(
                            userId = specification.userId,
                            pageCursor = pageCursor,
                            excludeReplies = true,
                        )?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filter { it.inReplyTo == null }
                        ?.deduplicate()
                        ?.fixupCreatorEmojis()
                        .orEmpty()
            }
        mutex.withLock {
            history.addAll(results)
        }

        // return a copy
        return history.map { it }
    }

    override fun extractState(): TimelinePaginationManagerState =
        DefaultTimelinePaginationManagerState(
            specification = specification,
            history = history,
            pageCursor = pageCursor,
            canFetchMore = canFetchMore,
        )

    override suspend fun restoreState(state: TimelinePaginationManagerState) {
        (state as? DefaultTimelinePaginationManagerState)?.also {
            specification = it.specification
            pageCursor = it.pageCursor
            canFetchMore = it.canFetchMore
            mutex.withLock {
                history.clear()
                history.addAll(it.history)
            }
        }
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

    private fun List<TimelineEntryModel>.filterReplies(included: Boolean): List<TimelineEntryModel> =
        filter {
            included || it.inReplyTo == null
        }

    private fun List<TimelineEntryModel>.filterNsfw(included: Boolean): List<TimelineEntryModel> = filter { included || !it.isNsfw }

    private suspend fun List<TimelineEntryModel>.fixupCreatorEmojis(): List<TimelineEntryModel> =
        with(emojiHelper) {
            map {
                it.withEmojisIfMissing()
            }
        }
}
