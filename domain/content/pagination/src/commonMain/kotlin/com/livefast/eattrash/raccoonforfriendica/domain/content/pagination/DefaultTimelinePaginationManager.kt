package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FollowedHashtagCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.PagedList
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
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

@ContributesBinding(scope = AppScope::class, binding = binding<TimelinePaginationManager>())
@Inject
class DefaultTimelinePaginationManager(
    private val timelineRepository: TimelineRepository,
    private val postRepository: PostRepository,
    private val accountRepository: AccountRepository,
    private val userRateLimitRepository: UserRateLimitRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val postActionHelper: PostActionHelper,
    private val stopWordRepository: StopWordRepository,
    private val followedHashtagCache: FollowedHashtagCache,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<PostModel, TimelinePaginationSpecification>(
    idSelector = { it.id },
),
    TimelinePaginationManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val userRateLimits = mutableMapOf<String, Double>()
    private var stopWords: List<String>? = null

    init {
        notificationCenter
            .subscribe(PostUpdatedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    updateHistoryItem(event.post.id) { event.post }
                }
            }.launchIn(scope)
        notificationCenter
            .subscribe(PostDeletedEvent::class)
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

    override suspend fun restoreHistory(values: List<PostModel>) {
        withPaginationLock {
            restorePaginationState(
                specification = currentSpecification,
                pageCursor = currentPageCursor,
                history = values,
            )
        }
    }

    override suspend fun loadNextPage(): List<PostModel> {
        val spec = currentSpecification ?: return emptyList()

        val results: PagedList<PostModel>? =
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
                    }?.toPagedList()
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
                    postRepository
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
                        )?.toPagedList()

                is TimelinePaginationSpecification.Forum ->
                    postRepository
                        .getByUser(
                            userId = spec.userId,
                            pageCursor = currentPageCursor,
                            excludeReplies = true,
                            otherInstance = spec.otherInstance,
                        )?.toPagedList()

                is TimelinePaginationSpecification.Bookmarks ->
                    postRepository
                        .getBookmarks(pageCursor = currentPageCursor)

                is TimelinePaginationSpecification.Favorites ->
                    postRepository
                        .getFavorites(pageCursor = currentPageCursor)

                is TimelinePaginationSpecification.Quotes ->
                    postRepository
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
                            .map { post ->
                                post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                }.let {
                                    it.copy(
                                        tags = it.tags.map { tag ->
                                            tag.copy(following = followedHashtagCache.isFollowed(tag))
                                        },
                                    )
                                }
                            }

                    is TimelinePaginationSpecification.Hashtag ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterWithRateLimits()
                            .filterByStopWords()
                            .map { post ->
                                post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                }
                            }

                    is TimelinePaginationSpecification.User ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterByStopWords()
                            .map { post ->
                                post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                }
                            }

                    is TimelinePaginationSpecification.Forum ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filter { it.reblog != null && it.reblog?.inReplyTo == null }
                            .filterByStopWords()
                            .map { post ->
                                post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                }
                            }

                    is TimelinePaginationSpecification.Bookmarks ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterByStopWords()
                            .map { post ->
                                post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                }
                            }

                    is TimelinePaginationSpecification.Favorites ->
                        newItems
                            .filterNsfw(spec.includeNsfw)
                            .filterByStopWords()
                            .map { post ->
                                post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                }
                            }

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

    private fun List<PostModel>.toPagedList(): PagedList<PostModel> {
        val cursor = lastOrNull()?.id
        return PagedList(list = this, cursor = cursor)
    }

    private fun List<PostModel>.filterWithRateLimits(): List<PostModel> = filterIndexed { index, post ->
        val creator = post.creator ?: return@filterIndexed true
        val rateLimit = userRateLimits[creator.handle] ?: return@filterIndexed true
        val postsByThisUserInHistory = history.count { it.creator?.id == creator.id }
        val postsByThisUserSoFar =
            subList(0, index).count { it.creator?.id == creator.id }
        val total = history.size + index
        if (total == 0) {
            return@filterIndexed true
        }
        val rate =
            (postsByThisUserInHistory + postsByThisUserSoFar + 1).toDouble() / (total + 1)
        rate <= rateLimit
    }

    private fun List<PostModel>.filterReplies(included: Boolean): List<PostModel> = filter {
        included || it.inReplyTo == null
    }

    private fun List<PostModel>.filterNsfw(included: Boolean): List<PostModel> = filter {
        included || !it.isNsfw
    }

    private fun List<PostModel>.filterByStopWords(): List<PostModel> = filter { post ->
        stopWords?.takeIf { it.isNotEmpty() }?.let { stopWordList ->
            stopWordList.none { word ->
                val postContents =
                    listOfNotNull(
                        post.content,
                        post.title,
                        post.reblog?.content,
                        post.reblog?.title,
                    )
                postContents.any { it.contains(other = word, ignoreCase = true) }
            }
        } ?: true
    }
}
