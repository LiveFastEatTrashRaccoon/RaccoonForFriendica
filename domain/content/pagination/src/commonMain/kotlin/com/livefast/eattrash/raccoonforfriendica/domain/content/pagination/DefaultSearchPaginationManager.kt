package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
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

internal class DefaultSearchPaginationManager(
    private val searchRepository: SearchRepository,
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val accountRepository: AccountRepository,
    private val stopWordRepository: StopWordRepository,
    notificationCenter: NotificationCenter = getNotificationCenter(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SearchPaginationManager {
    private var specification: SearchPaginationSpecification? = null
    override var canFetchMore: Boolean = true
    private var pageCursor: String? = null
    private val history = mutableListOf<ExploreItemModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var stopWords: List<String>? = null

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

    override suspend fun reset(specification: SearchPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        mutex.withLock {
            history.clear()
            accountRepository.getActive()?.id?.also { accountId ->
                stopWords = stopWordRepository.get(accountId)
            }
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
                        )?.filterNsfw(specification.includeNsfw)

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
                        )?.determineUserRelationshipStatus()
            }?.filterByStopWords()
                ?.deduplicate()
                ?.updatePaginationData()
                ?.fixupCreatorEmojis()
                ?.fixupInReplyTo()
                .orEmpty()
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

    private suspend fun List<ExploreItemModel>.fixupInReplyTo(): List<ExploreItemModel> =
        with(replyHelper) {
            map {
                when (it) {
                    is ExploreItemModel.Entry -> it.copy(entry = it.entry.withInReplyToIfMissing())
                    else -> it
                }
            }
        }

    private fun List<ExploreItemModel>.filterByStopWords(): List<ExploreItemModel> =
        filter { item ->
            when (item) {
                is ExploreItemModel.Entry ->
                    stopWords?.takeIf { it.isNotEmpty() }?.let { stopWordList ->
                        stopWordList.none { word ->
                            val entryTexts =
                                listOfNotNull(
                                    item.entry.content,
                                    item.entry.title,
                                    item.entry.reblog?.content,
                                    item.entry.reblog?.title,
                                )
                            entryTexts.any { it.contains(other = word, ignoreCase = true) }
                        }
                    } ?: true

                else -> true
            }
        }
}
