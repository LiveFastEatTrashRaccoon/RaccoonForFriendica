package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
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
import org.koin.core.annotation.Factory

@Factory
internal class DefaultTimelinePaginationManager(
    private val timelineRepository: TimelineRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val accountRepository: AccountRepository,
    private val userRateLimitRepository: UserRateLimitRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val stopWordRepository: StopWordRepository,
    private val followedHashtagCache: FollowedHashtagCache,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<TimelineEntryModel, TimelinePaginationSpecification>(
    idSelector = { it.id },
),
    TimelinePaginationManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val userRateLimits = mutableMapOf<String, Double>()
    private var stopWords: List<String>? = null

    init {
        notificationCenter
            .subscribe(TimelineEntryUpdatedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    updateHistoryItem(event.entry.id) { event.entry }
                }
            }.launchIn(scope)
        notificationCenter
            .subscribe(TimelineEntryDeletedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    removeHistoryItem(event.id)
                }
            }.launchIn(scope)
    }

    override suspend fun reset(specification: TimelinePaginationSpecification) {
        super.reset(specification)
        withPaginationLock {
            userRateLimits.clear()
            accountRepository.getActive()?.id?.also { accountId ->
                userRateLimitRepository.getAll(accountId).forEach { limit ->
                    userRateLimits[limit.handle] = limit.rate
                }
                stopWords = stopWordRepository.get(accountId)
            }
        }
    }

    override suspend fun restoreHistory(values: List<TimelineEntryModel>) {
        withPaginationLock {
            restorePaginationState(
                specification = currentSpecification,
                pageCursor = currentPageCursor,
                history = values,
            )
        }
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val spec = currentSpecification ?: return emptyList()

        val results: ListWithPageCursor<TimelineEntryModel>? =
            when (spec) {
                is TimelinePaginationSpecification.Feed -> {
                    when (spec.timelineType) {
                        is TimelineType.All ->
                            timelineRepository.getPublic(
                                pageCursor = currentPageCursor,
                                refresh = spec.refresh,
                            )

                        is TimelineType.Subscriptions ->
                            timelineRepository.getHome(
                                pageCursor = currentPageCursor,
                                refresh = spec.refresh,
                            )

                        is TimelineType.Local ->
                            timelineRepository.getLocal(
                                pageCursor = currentPageCursor,
                                refresh = spec.refresh,
                            )

                        is TimelineType.Circle ->
                            timelineRepository.getCircle(
                                id =
                                spec.timelineType.circle
                                    ?.id
                                    .orEmpty(),
                                pageCursor = currentPageCursor,
                            )

                        is TimelineType.Foreign ->
                            timelineRepository.getLocal(
                                pageCursor = currentPageCursor,
                                otherInstance = spec.timelineType.otherInstance,
                            )
                    }?.toListWithPageCursor()
                }

                is TimelinePaginationSpecification.Hashtag -> {
                    timelineRepository
                        .getHashtag(
                            hashtag = spec.hashtag,
                            pageCursor = currentPageCursor,
                            otherInstance = spec.otherInstance,
                        )
                }

                is TimelinePaginationSpecification.User ->
                    timelineEntryRepository
                        .getByUser(
                            userId = spec.userId,
                            pageCursor = currentPageCursor,
                            excludeReplies = spec.excludeReplies,
                            excludeReblogs = spec.excludeReblogs,
                            onlyMedia = spec.onlyMedia,
                            pinned = spec.pinned,
                            enableCache = spec.enableCache,
                            refresh = spec.refresh,
                            otherInstance = spec.otherInstance,
                        )?.toListWithPageCursor()

                is TimelinePaginationSpecification.Forum ->
                    timelineEntryRepository
                        .getByUser(
                            userId = spec.userId,
                            pageCursor = currentPageCursor,
                            excludeReplies = true,
                            otherInstance = spec.otherInstance,
                        )?.toListWithPageCursor()

                is TimelinePaginationSpecification.Bookmarks ->
                    timelineEntryRepository
                        .getBookmarks(pageCursor = currentPageCursor)

                is TimelinePaginationSpecification.Favorites ->
                    timelineEntryRepository
                        .getFavorites(pageCursor = currentPageCursor)

                is TimelinePaginationSpecification.Quotes ->
                    timelineEntryRepository
                        .getQuotes(
                            id = spec.id,
                            pageCursor = currentPageCursor,
                            otherInstance = spec.otherInstance,
                        )
            }

        return updateHistory(
            results = results,
            distinctBy = { it.safeKey },
            transform = { newItems ->
                when (spec) {
                    is TimelinePaginationSpecification.Feed ->
                        newItems
                            .filterReplies(included = !spec.excludeReplies)
                            .filterNsfw(spec.includeNsfw)
                            .filterWithRateLimits()
                            .filterByStopWords()
                            .fixupCreatorEmojis()
                            .fixupInReplyTo()
                            .fixupFollowedHashtags()

                    is TimelinePaginationSpecification.Hashtag ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterWithRateLimits()
                            .filterByStopWords()
                            .fixupCreatorEmojis()
                            .fixupInReplyTo()

                    is TimelinePaginationSpecification.User ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterByStopWords()
                            .fixupCreatorEmojis()
                            .fixupInReplyTo()

                    is TimelinePaginationSpecification.Forum ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filter { it.reblog != null && it.reblog?.inReplyTo == null }
                            .filterByStopWords()
                            .fixupCreatorEmojis()

                    is TimelinePaginationSpecification.Bookmarks ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterByStopWords()
                            .fixupCreatorEmojis()
                            .fixupInReplyTo()

                    is TimelinePaginationSpecification.Favorites ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterByStopWords()
                            .fixupCreatorEmojis()
                            .fixupInReplyTo()

                    is TimelinePaginationSpecification.Quotes ->
                        newItems
                }
            },
        )
    }

    override fun extractState(): TimelinePaginationManagerState = DefaultTimelinePaginationManagerState(
        specification = currentSpecification,
        pageCursor = currentPageCursor,
        history = history.toList(),
        userRateLimits = userRateLimits.toMap(),
        stopWords = stopWords,
    )

    override fun restoreState(state: TimelinePaginationManagerState) {
        (state as? DefaultTimelinePaginationManagerState)?.also {
            restorePaginationState(
                specification = it.specification,
                pageCursor = it.pageCursor,
                history = it.history,
            )
            userRateLimits.clear()
            userRateLimits.putAll(it.userRateLimits)
            stopWords = it.stopWords
        }
    }

    private fun List<TimelineEntryModel>.toListWithPageCursor(): ListWithPageCursor<TimelineEntryModel> = let { list ->
        val cursor = list.lastOrNull()?.id
        ListWithPageCursor(list = list, cursor = cursor)
    }

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

    private fun List<TimelineEntryModel>.filterReplies(included: Boolean): List<TimelineEntryModel> = filter {
        included || it.inReplyTo == null
    }

    private fun List<TimelineEntryModel>.filterNsfw(included: Boolean): List<TimelineEntryModel> = filter {
        included || !it.isNsfw
    }

    private suspend fun List<TimelineEntryModel>.fixupCreatorEmojis(): List<TimelineEntryModel> = with(emojiHelper) {
        map {
            it.withEmojisIfMissing()
        }
    }

    private suspend fun List<TimelineEntryModel>.fixupInReplyTo(): List<TimelineEntryModel> = with(replyHelper) {
        map {
            it.withInReplyToIfMissing()
        }
    }

    private fun List<TimelineEntryModel>.filterByStopWords(): List<TimelineEntryModel> = filter { entry ->
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

    private suspend fun List<TimelineEntryModel>.fixupFollowedHashtags(): List<TimelineEntryModel> = map { entry ->
        val tags = entry.tags.map { tag ->
            tag.copy(following = followedHashtagCache.isFollowed(tag))
        }
        entry.copy(tags = tags)
    }
}
