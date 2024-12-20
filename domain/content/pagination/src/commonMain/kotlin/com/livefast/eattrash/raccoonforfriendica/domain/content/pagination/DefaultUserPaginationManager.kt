package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultUserPaginationManager(
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val circlesRepository: CirclesRepository,
    private val emojiHelper: EmojiHelper,
    notificationCenter: NotificationCenter = getNotificationCenter(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UserPaginationManager {
    private var specification: UserPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<UserModel>()
    private val mutex = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        notificationCenter
            .subscribe(UserUpdatedEvent::class)
            .onEach { event ->
                mutex.withLock {
                    val idx = history.indexOfFirst { u -> u.id == event.user.id }
                    if (idx >= 0) {
                        history[idx] = event.user
                    }
                }
            }.launchIn(scope)
    }

    override suspend fun reset(specification: UserPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        mutex.withLock {
            history.clear()
        }
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<UserModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is UserPaginationSpecification.Follower ->
                    userRepository
                        .getFollowers(
                            id = specification.userId,
                            pageCursor = pageCursor,
                        )

                is UserPaginationSpecification.Following ->
                    userRepository
                        .getFollowing(
                            id = specification.userId,
                            pageCursor = pageCursor,
                        )

                is UserPaginationSpecification.EntryUsersFavorite ->
                    timelineEntryRepository
                        .getUsersWhoFavorited(
                            id = specification.entryId,
                            pageCursor = pageCursor,
                        )

                is UserPaginationSpecification.EntryUsersReblog ->
                    timelineEntryRepository
                        .getUsersWhoReblogged(
                            id = specification.entryId,
                            pageCursor = pageCursor,
                        )

                is UserPaginationSpecification.Search ->
                    userRepository
                        .search(
                            query = specification.query,
                            offset =
                                history
                                    .indexOfLast { it.id == pageCursor }
                                    .takeIf { it >= 0 }
                                    ?.let {
                                        // offset is count, not index
                                        it + 1
                                    } ?: 0,
                        )

                UserPaginationSpecification.Blocked ->
                    userRepository.getBlocked(pageCursor = pageCursor)

                UserPaginationSpecification.Muted ->
                    userRepository.getMuted(pageCursor = pageCursor)

                is UserPaginationSpecification.CircleMembers ->
                    circlesRepository
                        .getMembers(
                            id = specification.id,
                            pageCursor = pageCursor,
                        )

                is UserPaginationSpecification.SearchFollowing ->
                    userRepository
                        .searchMyFollowing(
                            query = specification.query,
                            pageCursor = pageCursor,
                        )
            }

        return mutex.withLock {
            // result post-processing
            when (specification) {
                is UserPaginationSpecification.Follower -> {
                    results
                        ?.deduplicate()
                        ?.determineRelationshipStatus()
                        ?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                is UserPaginationSpecification.Following -> {
                    results
                        ?.deduplicate()
                        ?.determineRelationshipStatus()
                        ?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                is UserPaginationSpecification.EntryUsersFavorite -> {
                    results
                        ?.determineRelationshipStatus()
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                is UserPaginationSpecification.EntryUsersReblog -> {
                    results
                        ?.deduplicate()
                        ?.determineRelationshipStatus()
                        ?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                is UserPaginationSpecification.Search -> {
                    results
                        ?.deduplicate()
                        ?.let {
                            if (specification.withRelationship) {
                                it.determineRelationshipStatus()
                            } else {
                                it
                            }
                        }?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                UserPaginationSpecification.Blocked -> {
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                UserPaginationSpecification.Muted -> {
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.fixupCreatorEmojis()
                }

                is UserPaginationSpecification.CircleMembers -> {
                    results
                        ?.deduplicate()
                        ?.updatePaginationData()
                        ?.filter(specification.query)
                        ?.fixupCreatorEmojis()
                }

                is UserPaginationSpecification.SearchFollowing -> {
                    results
                        ?.deduplicate()
                        ?.let {
                            if (specification.withRelationship) {
                                it.determineRelationshipStatus()
                            } else {
                                it
                            }
                        }?.updatePaginationData()
                        ?.filter {
                            it.id !in specification.excludeIds
                        }?.fixupCreatorEmojis()
                }
            }.orEmpty().also { history.addAll(it) }
            // return a copy
            history.map { it }
        }
    }

    private fun List<UserModel>.updatePaginationData(): List<UserModel> =
        apply {
            lastOrNull()?.also {
                pageCursor = it.id
            }
            canFetchMore = isNotEmpty()
        }

    private suspend fun List<UserModel>.determineRelationshipStatus(): List<UserModel> =
        run {
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

    private fun List<UserModel>.deduplicate(): List<UserModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }

    private fun List<UserModel>.filter(query: String): List<UserModel> =
        filter {
            query.isEmpty() ||
                it.displayName?.contains(query, ignoreCase = true) == true ||
                it.username?.contains(query, ignoreCase = true) == true
        }

    private suspend fun List<UserModel>.fixupCreatorEmojis(): List<UserModel> =
        with(emojiHelper) {
            map {
                it.withEmojisIfMissing()
            }
        }
}
