package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
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
import kotlinx.coroutines.launch

@ContributesBinding(scope = AppScope::class, binding = binding<SearchPaginationManager>())
@Inject
class DefaultSearchPaginationManager(
    private val searchRepository: SearchRepository,
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val postActionHelper: PostActionHelper,
    private val accountRepository: AccountRepository,
    private val stopWordRepository: StopWordRepository,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<ExploreItemModel, SearchPaginationSpecification>(
    idSelector = { it.id },
),
    SearchPaginationManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var stopWords: List<String>? = null

    init {
        scope.launch {
            notificationCenter
                .subscribe(UserUpdatedEvent::class)
                .onEach { event ->
                    withPaginationLock {
                        updateHistoryItem(event.user.id) { item ->
                            (item as? ExploreItemModel.User)?.copy(user = event.user) ?: item
                        }
                    }
                }.launchIn(this)
            notificationCenter
                .subscribe(PostUpdatedEvent::class)
                .onEach { event ->
                    withPaginationLock {
                        updateHistoryItem(event.post.id) { item ->
                            (item as? ExploreItemModel.Post)?.copy(post = event.post) ?: item
                        }
                    }
                }.launchIn(this)
            notificationCenter
                .subscribe(PostDeletedEvent::class)
                .onEach { event ->
                    withPaginationLock {
                        removeHistoryItem(event.id)
                    }
                }.launchIn(this)
        }
    }

    override suspend fun reset(specification: SearchPaginationSpecification) {
        super.reset(specification)
        withPaginationLock {
            accountRepository.getActive()?.id?.also { accountId ->
                stopWords = stopWordRepository.get(accountId)
            }
        }
    }

    override suspend fun loadNextPage(): List<ExploreItemModel> {
        val spec = currentSpecification ?: return emptyList()
        val results =
            when (spec) {
                is SearchPaginationSpecification.Posts ->
                    searchRepository
                        .search(
                            query = spec.query,
                            pageCursor = currentPageCursor,
                            type = SearchResultType.Posts,
                            resolve = guessResolvePost(spec.query),
                        )

                is SearchPaginationSpecification.Hashtags ->
                    searchRepository
                        .search(
                            query = spec.query,
                            pageCursor = currentPageCursor,
                            type = SearchResultType.Hashtags,
                        )

                is SearchPaginationSpecification.Users ->
                    searchRepository
                        .search(
                            query = spec.query,
                            pageCursor = currentPageCursor,
                            type = SearchResultType.Users,
                            resolve = guessResolveUser(spec.query),
                        )?.determineUserRelationshipStatus()

                is SearchPaginationSpecification.Groups ->
                    searchRepository.search(
                        query = spec.query,
                        pageCursor = currentPageCursor,
                        type = SearchResultType.Users,
                        resolve = guessResolveUser(spec.query),
                    )
            }

        // use the calculated offset as cursor
        val cursor = if (results.isNullOrEmpty()) null else (history.size + results.size).toString()

        return updateHistory(
            results = PagedList(
                list = results.orEmpty(),
                cursor = cursor,
            ),
            transform = { newItems ->
                newItems
                    .filterByStopWords()
                    .let { list ->
                        when (spec) {
                            is SearchPaginationSpecification.Posts -> list.filterNsfw(spec.includeNsfw)
                            is SearchPaginationSpecification.Groups -> list.filterGroups()
                            else -> list
                        }
                    }.map { item ->
                        when (item) {
                            is ExploreItemModel.Post -> item.copy(
                                post = item.post.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                }.let {
                                    with(replyHelper) { it.withInReplyToIfMissing() }
                                }.let {
                                    with(postActionHelper) { it.withActions() }
                                },
                            )

                            is ExploreItemModel.User -> item.copy(
                                user = item.user.let {
                                    with(emojiHelper) { it.withEmojisIfMissing() }
                                },
                            )

                            is ExploreItemModel.HashTag -> item

                            is ExploreItemModel.Link -> item
                        }
                    }
            },
        )
    }

    private fun List<ExploreItemModel>.filterNsfw(included: Boolean): List<ExploreItemModel> = filter {
        included || !it.isNsfw
    }

    private suspend fun List<ExploreItemModel>.determineUserRelationshipStatus(): List<ExploreItemModel> = run {
        val userIds = mapNotNull { item -> (item as? ExploreItemModel.User)?.user?.id }
        val relationships = userRepository.getRelationships(userIds)
        map { item ->
            if (item !is ExploreItemModel.User) {
                item
            } else {
                val relationship = relationships?.firstOrNull { rel -> rel.id == item.user.id }
                item.copy(
                    user =
                    item.user.copy(
                        relationshipStatus = relationship?.toStatus(),
                        notificationStatus = relationship?.toNotificationStatus(),
                    ),
                )
            }
        }
    }

    private fun List<ExploreItemModel>.filterByStopWords(): List<ExploreItemModel> = filter { item ->
        when (item) {
            is ExploreItemModel.Post ->
                stopWords?.takeIf { it.isNotEmpty() }?.let { stopWordList ->
                    stopWordList.none { word ->
                        val postContents =
                            listOfNotNull(
                                item.post.content,
                                item.post.title,
                                item.post.reblog?.content,
                                item.post.reblog?.title,
                            )
                        postContents.any { it.contains(other = word, ignoreCase = true) }
                    }
                } != false

            else -> true
        }
    }

    private fun List<ExploreItemModel>.filterGroups(): List<ExploreItemModel> = filter {
        if (it is ExploreItemModel.User) {
            it.user.group
        } else {
            false
        }
    }

    private fun guessResolvePost(query: String): Boolean = MASTODON_POST_REGEX.matches(query.trim()) ||
        FRIENDICA_POST_REGEX.matches(query.trim())

    private fun guessResolveUser(query: String): Boolean = MASTODON_USER_REGEX.matches(query.trim()) ||
        FRIENDICA_USER_REGEX.matches(query.trim())

    private companion object {
        private val MASTODON_POST_REGEX =
            Regex("^https?://[^/]+/@([\\w.\\-]+)/(\\d+)/?$", RegexOption.IGNORE_CASE)
        private val MASTODON_USER_REGEX =
            Regex("^https?://[^/]+/@([\\w.\\-]+)/?$", RegexOption.IGNORE_CASE)
        private val FRIENDICA_POST_REGEX =
            Regex("^https?://[^/]+/display/([\\w.\\-]+)/?$", RegexOption.IGNORE_CASE)
        private val FRIENDICA_USER_REGEX =
            Regex("^https?://[^/]+/contact/(\\d+)(/.*)?$", RegexOption.IGNORE_CASE)
    }
}
