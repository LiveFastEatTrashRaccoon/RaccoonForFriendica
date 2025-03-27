package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FollowedHashtagCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
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
    private val accountRepository: AccountRepository,
    private val userRateLimitRepository: UserRateLimitRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val stopWordRepository: StopWordRepository,
    private val followedHashtagCache: FollowedHashtagCache,
    notificationCenter: NotificationCenter = getNotificationCenter(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TimelinePaginationManager {
    private var specification: TimelinePaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    override val history = mutableListOf<TimelineEntryModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val userRateLimits = mutableMapOf<String, Double>()
    private var stopWords: List<String>? = null

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
            userRateLimits.clear()
            accountRepository.getActive()?.id?.also { accountId ->
                userRateLimitRepository.getAll(accountId).forEach { limit ->
                    userRateLimits[limit.handle] = limit.rate
                }
                stopWords = stopWordRepository.get(accountId)
            }
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun restoreHistory(values: List<TimelineEntryModel>) {
        mutex.withLock {
            history.clear()
            history.addAll(values)
            canFetchMore = true
        }
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is TimelinePaginationSpecification.Feed -> {
                    when (specification.timelineType) {
                        is TimelineType.All ->
                            timelineRepository.getPublic(
                                pageCursor = pageCursor,
                                refresh = specification.refresh,
                            )

                        is TimelineType.Subscriptions ->
                            timelineRepository.getHome(
                                pageCursor = pageCursor,
                                refresh = specification.refresh,
                            )

                        is TimelineType.Local ->
                            timelineRepository.getLocal(
                                pageCursor = pageCursor,
                                refresh = specification.refresh,
                            )

                        is TimelineType.Circle ->
                            timelineRepository.getCircle(
                                id =
                                    specification.timelineType.circle
                                        ?.id
                                        .orEmpty(),
                                pageCursor = pageCursor,
                            )

                        is TimelineType.Foreign ->
                            timelineRepository.getLocal(
                                pageCursor = pageCursor,
                                otherInstance = specification.timelineType.node,
                            )
                    }?.toListWithPageCursor()
                }

                is TimelinePaginationSpecification.Hashtag -> {
                    timelineRepository
                        .getHashtag(
                            hashtag = specification.hashtag,
                            pageCursor = pageCursor,
                        )
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
                            enableCache = specification.enableCache,
                            refresh = specification.refresh,
                        )?.toListWithPageCursor()

                is TimelinePaginationSpecification.Forum ->
                    timelineEntryRepository
                        .getByUser(
                            userId = specification.userId,
                            pageCursor = pageCursor,
                            excludeReplies = true,
                        )?.toListWithPageCursor()

                is TimelinePaginationSpecification.Bookmarks ->
                    timelineEntryRepository
                        .getBookmarks(pageCursor = pageCursor)
                        ?.toListWithPageCursor()

                is TimelinePaginationSpecification.Favorites ->
                    timelineEntryRepository
                        .getFavorites(pageCursor = pageCursor)
                        ?.toListWithPageCursor()
            }
        return mutex.withLock {
            when (specification) {
                is TimelinePaginationSpecification.Feed ->
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterReplies(included = !specification.excludeReplies)
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filterWithRateLimits()
                        ?.filterByStopWords()
                        ?.fixupCreatorEmojis()
                        ?.fixupInReplyTo()
                        ?.fixumFollowedHashtags()

                is TimelinePaginationSpecification.Hashtag ->
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filterWithRateLimits()
                        ?.filterByStopWords()
                        ?.fixupCreatorEmojis()
                        ?.fixupInReplyTo()

                is TimelinePaginationSpecification.User ->
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filterByStopWords()
                        ?.fixupCreatorEmojis()
                        ?.fixupInReplyTo()

                is TimelinePaginationSpecification.Forum ->
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filter { it.reblog != null && it.reblog?.inReplyTo == null }
                        ?.filterByStopWords()
                        ?.fixupCreatorEmojis()

                is TimelinePaginationSpecification.Bookmarks ->
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filterByStopWords()
                        ?.fixupCreatorEmojis()
                        ?.fixupInReplyTo()

                is TimelinePaginationSpecification.Favorites ->
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        ?.filterByStopWords()
                        ?.fixupCreatorEmojis()
                        ?.fixupInReplyTo()
            }.orEmpty().also { history.addAll(it) }
            history.map { it }
        }
    }

    override fun extractState(): TimelinePaginationManagerState =
        DefaultTimelinePaginationManagerState(
            specification = specification,
            pageCursor = pageCursor,
            history = history,
            userRateLimits = userRateLimits,
            stopWords = stopWords,
        )

    override fun restoreState(state: TimelinePaginationManagerState) {
        (state as? DefaultTimelinePaginationManagerState)?.also {
            specification = it.specification
            pageCursor = it.pageCursor
            history.clear()
            history.addAll(it.history)
            userRateLimits.putAll(it.userRateLimits)
            stopWords = it.stopWords
        }
    }

    private fun List<TimelineEntryModel>.toListWithPageCursor(): ListWithPageCursor<TimelineEntryModel> =
        let { list ->
            val cursor = list.lastOrNull()?.id
            ListWithPageCursor(list = list, cursor = cursor)
        }

    private fun ListWithPageCursor<TimelineEntryModel>.updatePaginationData(): List<TimelineEntryModel> =
        run {
            pageCursor = cursor
            canFetchMore = list.isNotEmpty()
            list
        }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.safeKey }

    private fun List<TimelineEntryModel>.filterWithRateLimits(): List<TimelineEntryModel> =
        filterIndexed { index, timelineEntryModel ->
            val creator = timelineEntryModel.creator ?: return@filterIndexed true
            val rateLimit = userRateLimits[creator.handle] ?: return@filterIndexed true
            val entriesByThisUserInHistory = history.count { it.creator?.id == creator.id }
            val entriesByThisUserSoFar =
                subList(0, index).count { it.creator?.id == creator.id }
            val total = history.size + index
            check(total != 0) { return@filterIndexed true }
            val rate =
                (entriesByThisUserInHistory + entriesByThisUserSoFar + 1).toDouble() / (total + 1)
            rate <= rateLimit
        }

    private fun ListWithPageCursor<TimelineEntryModel>.deduplicate(): ListWithPageCursor<TimelineEntryModel> =
        run {
            val newList = list.deduplicate()
            ListWithPageCursor(
                list = newList,
                cursor = newList.lastOrNull()?.id,
            )
        }

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

    private suspend fun List<TimelineEntryModel>.fixupInReplyTo(): List<TimelineEntryModel> =
        with(replyHelper) {
            map {
                it.withInReplyToIfMissing()
            }
        }

    private fun List<TimelineEntryModel>.filterByStopWords(): List<TimelineEntryModel> =
        filter { entry ->
            stopWords?.takeIf { it.isNotEmpty() }?.let { stopWordList ->
                stopWordList.none { word ->
                    val entryTexts =
                        listOfNotNull(
                            entry.content,
                            entry.title,
                            entry.reblog?.content,
                            entry.reblog?.title,
                        )
                    entryTexts.any { it.contains(other = word, ignoreCase = true) }
                }
            } ?: true
        }

    private suspend fun List<TimelineEntryModel>.fixumFollowedHashtags(): List<TimelineEntryModel> =
        this.map { entry ->
            val tags =
                entry.tags.map { tag ->
                    tag.copy(following = followedHashtagCache.isFollowed(tag))
                }
            entry.copy(tags = tags)
        }
}
