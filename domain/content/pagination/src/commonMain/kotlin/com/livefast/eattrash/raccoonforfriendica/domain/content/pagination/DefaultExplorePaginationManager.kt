package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultExplorePaginationManager(
    private val trendingRepository: TrendingRepository,
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ExplorePaginationManager {
    private var specification: ExplorePaginationSpecification? = null
    private var offset = 0
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<ExploreItemModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        scope.launch {
            notificationCenter
                .subscribe(UserUpdatedEvent::class)
                .onEach { event ->
                    mutex.withLock {
                        val idx =
                            history.indexOfFirst { e -> e is ExploreItemModel.User && e.user.id == event.user.id }
                        if (idx >= 0) {
                            (history[idx] as? ExploreItemModel.User)
                                ?.copy(user = event.user)
                                ?.also {
                                    history[idx] = it
                                }
                        }
                    }
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    mutex.withLock {
                        val idx =
                            history.indexOfFirst { e -> e is ExploreItemModel.Entry && e.entry.id == event.entry.id }
                        if (idx >= 0) {
                            (history[idx] as? ExploreItemModel.Entry)
                                ?.copy(entry = event.entry)
                                ?.also {
                                    history[idx] = it
                                }
                        }
                    }
                }.launchIn(this)
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
    }

    override suspend fun reset(specification: ExplorePaginationSpecification) {
        this.specification = specification
        offset = 0
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<ExploreItemModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is ExplorePaginationSpecification.Hashtags ->
                    trendingRepository
                        .getHashtags(
                            offset = offset,
                            refresh = specification.refresh,
                        )?.map {
                            ExploreItemModel.HashTag(it)
                        }

                ExplorePaginationSpecification.Links ->
                    trendingRepository.getLinks(offset)?.mapNotNull {
                        if (it.url.isBlank()) {
                            null
                        } else {
                            ExploreItemModel.Link(it)
                        }
                    }

                is ExplorePaginationSpecification.Posts ->
                    trendingRepository
                        .getEntries(offset)
                        ?.map {
                            ExploreItemModel.Entry(it)
                        }?.filterNsfw(specification.includeNsfw)

                ExplorePaginationSpecification.Suggestions ->
                    userRepository
                        .getSuggestions()
                        ?.map {
                            ExploreItemModel.User(it)
                        }?.determineUserRelationshipStatus()
            }?.deduplicate()
                ?.updatePaginationData()
                ?.fixupCreatorEmojis()
                .orEmpty()
        mutex.withLock {
            history.addAll(results)
        }

        // return a copy
        return history.map { it }
    }

    private fun List<ExploreItemModel>.updatePaginationData(): List<ExploreItemModel> =
        apply {
            offset = size
            canFetchMore = isNotEmpty()
        }

    private fun List<ExploreItemModel>.deduplicate(): List<ExploreItemModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }

    private fun List<ExploreItemModel>.filterNsfw(included: Boolean): List<ExploreItemModel> = filter { included || !it.isNsfw }

    private suspend fun List<ExploreItemModel>.determineUserRelationshipStatus(): List<ExploreItemModel> =
        run {
            val userIds = mapNotNull { e -> (e as? ExploreItemModel.User)?.user?.id }
            val relationships = userRepository.getRelationships(userIds)
            map { entry ->
                if (entry !is ExploreItemModel.User) {
                    entry
                } else {
                    val relationship = relationships?.firstOrNull { rel -> rel.id == entry.user.id }
                    entry.copy(
                        user =
                            entry.user.copy(
                                relationshipStatus = relationship?.toStatus(),
                                notificationStatus = relationship?.toNotificationStatus(),
                            ),
                    )
                }
            }
        }

    private suspend fun List<ExploreItemModel>.fixupCreatorEmojis(): List<ExploreItemModel> =
        with(emojiHelper) {
            map {
                when (it) {
                    is ExploreItemModel.Entry -> it.copy(entry = it.entry.withEmojisIfMissing())
                    is ExploreItemModel.User -> it.copy(user = it.user.withEmojisIfMissing())
                    else -> it
                }
            }
        }
}
