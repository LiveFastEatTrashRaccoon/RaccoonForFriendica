package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory

@Factory
internal class DefaultUserPaginationManager(
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val circlesRepository: CirclesRepository,
    private val accountRepository: AccountRepository,
    private val userRateLimitRepository: UserRateLimitRepository,
    private val emojiHelper: EmojiHelper,
    notificationCenter: NotificationCenter,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BasePaginationManager<UserModel, UserPaginationSpecification>(
    idSelector = { it.id },
),
    UserPaginationManager {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(UserUpdatedEvent::class)
            .onEach { event ->
                withPaginationLock {
                    updateHistoryItem(event.user.id) { event.user }
                }
            }.launchIn(scope)
    }

    override suspend fun loadNextPage(): List<UserModel> {
        val spec = currentSpecification ?: return emptyList()

        val results: ListWithPageCursor<UserModel>? =
            when (spec) {
                is UserPaginationSpecification.Follower ->
                    userRepository
                        .getFollowers(
                            id = spec.userId,
                            pageCursor = currentPageCursor,
                        )?.toListWithPageCursor()

                is UserPaginationSpecification.Following ->
                    userRepository
                        .getFollowing(
                            id = spec.userId,
                            pageCursor = currentPageCursor,
                        )?.toListWithPageCursor()

                is UserPaginationSpecification.EntryUsersFavorite ->
                    timelineEntryRepository
                        .getUsersWhoFavorited(
                            id = spec.entryId,
                            pageCursor = currentPageCursor,
                        )?.toListWithPageCursor()

                is UserPaginationSpecification.EntryUsersReblog ->
                    timelineEntryRepository
                        .getUsersWhoReblogged(
                            id = spec.entryId,
                            pageCursor = currentPageCursor,
                        )?.toListWithPageCursor()

                is UserPaginationSpecification.Search ->
                    userRepository
                        .search(
                            query = spec.query,
                            offset = history.size,
                        )?.toListWithPageCursor()

                UserPaginationSpecification.Blocked ->
                    userRepository.getBlocked(pageCursor = currentPageCursor)?.toListWithPageCursor()

                UserPaginationSpecification.Muted ->
                    userRepository.getMuted(pageCursor = currentPageCursor)?.toListWithPageCursor()

                is UserPaginationSpecification.CircleMembers ->
                    circlesRepository
                        .getMembers(
                            id = spec.id,
                            pageCursor = currentPageCursor,
                        )?.toListWithPageCursor()

                is UserPaginationSpecification.SearchFollowing ->
                    userRepository
                        .searchMyFollowing(
                            query = spec.query,
                            pageCursor = currentPageCursor,
                        )?.toListWithPageCursor()

                UserPaginationSpecification.Limited -> {
                    val accountId = accountRepository.getActive()?.id ?: 0
                    val rates = userRateLimitRepository.getAll(accountId)
                    val list = rates.map {
                        UserModel(
                            id = it.handle,
                            handle = it.handle,
                            displayName = it.handle,
                            username = "${it.rate}",
                        )
                    }
                    ListWithPageCursor(list = list, cursor = null)
                }
            }

        return updateHistory(
            results = results,
            transform = { newItems ->
                when (spec) {
                    is UserPaginationSpecification.Follower,
                    is UserPaginationSpecification.Following,
                    is UserPaginationSpecification.EntryUsersFavorite,
                    is UserPaginationSpecification.EntryUsersReblog,
                    UserPaginationSpecification.Blocked,
                    UserPaginationSpecification.Muted,
                    -> {
                        newItems
                            .determineRelationshipStatus()
                            .fixupCreatorEmojis()
                    }

                    is UserPaginationSpecification.Search -> {
                        newItems.let {
                            if (spec.withRelationship) {
                                it.determineRelationshipStatus()
                            } else {
                                it
                            }
                        }.fixupCreatorEmojis()
                    }

                    is UserPaginationSpecification.CircleMembers -> {
                        newItems
                            .filter(spec.query)
                            .fixupCreatorEmojis()
                    }

                    is UserPaginationSpecification.SearchFollowing -> {
                        newItems.let {
                            if (spec.withRelationship) {
                                it.determineRelationshipStatus()
                            } else {
                                it
                            }
                        }.filter {
                            it.id !in spec.excludeIds
                        }.fixupCreatorEmojis()
                    }

                    UserPaginationSpecification.Limited -> {
                        newItems
                    }
                }
            },
        )
    }

    private fun List<UserModel>.toListWithPageCursor(): ListWithPageCursor<UserModel> = let { list ->
        val cursor = list.lastOrNull()?.id
        ListWithPageCursor(list = list, cursor = cursor)
    }

    private suspend fun List<UserModel>.determineRelationshipStatus(): List<UserModel> = run {
        val userIds = map { user -> user.id }
        val relationships = userRepository.getRelationships(userIds)
        map { user ->
            val relationship = relationships?.firstOrNull { rel -> rel.id == user.id }
            user.copy(
                relationshipStatus = relationship?.toStatus(),
                notificationStatus = relationship?.toNotificationStatus(),
            )
        }
    }

    private fun List<UserModel>.filter(query: String): List<UserModel> = filter {
        query.isEmpty() ||
            it.displayName?.contains(query, ignoreCase = true) == true ||
            it.username?.contains(query, ignoreCase = true) == true
    }

    private suspend fun List<UserModel>.fixupCreatorEmojis(): List<UserModel> = with(emojiHelper) {
        map {
            it.withEmojisIfMissing()
        }
    }
}
