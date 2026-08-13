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
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
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
import org.koin.core.annotation.Factory

@Factory
internal class DefaultExplorePaginationManager(
    private val trendingRepository: TrendingRepository,
    private val userRepository: UserRepository,
    private val emojiHelper: EmojiHelper,
    private val replyHelper: ReplyHelper,
    private val accountRepository: AccountRepository,
    private val stopWordRepository: StopWordRepository,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<ExploreItemModel, ExplorePaginationSpecification>(
    idSelector = { it.id },
),
    ExplorePaginationManager {
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
                .subscribe(TimelineEntryUpdatedEvent::class)
                .onEach { event ->
                    withPaginationLock {
                        updateHistoryItem(event.entry.id) { item ->
                            (item as? ExploreItemModel.Entry)?.copy(entry = event.entry) ?: item
                        }
                    }
                }.launchIn(this)
            notificationCenter
                .subscribe(TimelineEntryDeletedEvent::class)
                .onEach { event ->
                    withPaginationLock {
                        removeHistoryItem(event.id)
                    }
                }.launchIn(this)
        }
    }

    override suspend fun reset(specification: ExplorePaginationSpecification) {
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
                is ExplorePaginationSpecification.Hashtags ->
                    trendingRepository
                        .getHashtags(
                            offset = history.size,
                            refresh = spec.refresh,
                            otherInstance = spec.otherInstance,
                        )?.map {
                            ExploreItemModel.HashTag(it)
                        }

                is ExplorePaginationSpecification.Links ->
                    trendingRepository.getLinks(
                        offset = history.size,
                        otherInstance = spec.otherInstance,
                    )?.mapNotNull {
                        if (it.url.isBlank()) {
                            null
                        } else {
                            ExploreItemModel.Link(it)
                        }
                    }

                is ExplorePaginationSpecification.Posts ->
                    trendingRepository
                        .getEntries(
                            offset = history.size,
                            otherInstance = spec.otherInstance,
                        )
                        ?.map {
                            ExploreItemModel.Entry(it)
                        }

                ExplorePaginationSpecification.Suggestions ->
                    userRepository
                        .getSuggestions()
                        ?.map {
                            ExploreItemModel.User(it)
                        }?.determineUserRelationshipStatus()
            }

        // for follow suggestions there is no pagination, just the first page
        val stopPagination = spec == ExplorePaginationSpecification.Suggestions && !currentPageCursor.isNullOrEmpty()

        return updateHistory(
            items = if (stopPagination) emptyList() else results,
            transform = { newItems ->
                newItems
                    .filterByStopWords()
                    .filterNsfw(included = if (spec is ExplorePaginationSpecification.Posts) spec.includeNsfw else true)
                    .fixupCreatorEmojis()
                    .fixupInReplyTo()
            },
        )
    }

    private fun List<ExploreItemModel>.filterNsfw(included: Boolean): List<ExploreItemModel> = filter {
        included || !it.isNsfw
    }

    private suspend fun List<ExploreItemModel>.determineUserRelationshipStatus(): List<ExploreItemModel> = run {
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

    private suspend fun List<ExploreItemModel>.fixupCreatorEmojis(): List<ExploreItemModel> = with(emojiHelper) {
        map {
            when (it) {
                is ExploreItemModel.Entry -> it.copy(entry = it.entry.withEmojisIfMissing())
                is ExploreItemModel.User -> it.copy(user = it.user.withEmojisIfMissing())
                else -> it
            }
        }
    }

    private suspend fun List<ExploreItemModel>.fixupInReplyTo(): List<ExploreItemModel> = with(replyHelper) {
        map {
            when (it) {
                is ExploreItemModel.Entry -> it.copy(entry = it.entry.withInReplyToIfMissing())
                else -> it
            }
        }
    }

    private fun List<ExploreItemModel>.filterByStopWords(): List<ExploreItemModel> = filter { item ->
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
