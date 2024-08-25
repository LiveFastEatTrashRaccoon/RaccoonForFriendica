package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository

internal class DefaultUserPaginationManager(
    private val userRepository: UserRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
    private val circlesRepository: CirclesRepository,
) : UserPaginationManager {
    private var specification: UserPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<UserModel>()

    override suspend fun reset(specification: UserPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
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
                        ).deduplicate()
                        .determineRelationshipStatus()
                        .updatePaginationData()

                is UserPaginationSpecification.Following ->
                    userRepository
                        .getFollowing(
                            id = specification.userId,
                            pageCursor = pageCursor,
                        ).deduplicate()
                        .determineRelationshipStatus()
                        .updatePaginationData()

                is UserPaginationSpecification.EntryUsersFavorite ->
                    timelineEntryRepository
                        .getUsersWhoFavorited(
                            id = specification.entryId,
                            pageCursor = pageCursor,
                        ).determineRelationshipStatus()
                        .deduplicate()
                        .updatePaginationData()

                is UserPaginationSpecification.EntryUsersReblog ->
                    timelineEntryRepository
                        .getUsersWhoReblogged(
                            id = specification.entryId,
                            pageCursor = pageCursor,
                        ).deduplicate()
                        .determineRelationshipStatus()
                        .updatePaginationData()

                is UserPaginationSpecification.Search ->
                    userRepository
                        .search(
                            query = specification.query,
                            offset =
                                history
                                    .indexOfLast { it.id == pageCursor }
                                    .takeIf { it >= 0 } ?: 0,
                        ).deduplicate()
                        .determineRelationshipStatus()
                        .updatePaginationData()

                UserPaginationSpecification.Blocked ->
                    userRepository
                        .getBlocked(
                            pageCursor = pageCursor,
                        ).deduplicate()
                        .updatePaginationData()

                UserPaginationSpecification.Muted ->
                    userRepository
                        .getMuted(
                            pageCursor = pageCursor,
                        ).deduplicate()
                        .updatePaginationData()

                is UserPaginationSpecification.CircleMembers ->
                    circlesRepository
                        .getMembers(
                            id = specification.id,
                            pageCursor = pageCursor,
                        ).deduplicate()
                        .updatePaginationData()
                        .filter(specification.query)
            }
        history.addAll(results)

        // return a copy
        return history.map { it }
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
                val relationship = relationships.firstOrNull { rel -> rel.id == user.id }
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
}
