package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toNotificationStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository

internal class DefaultNotificationsPaginationManager(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) : NotificationsPaginationManager {
    private var specification: NotificationsPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<NotificationModel>()

    override suspend fun reset(specification: NotificationsPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<NotificationModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is NotificationsPaginationSpecification.Default ->
                    notificationRepository
                        .getAll(
                            pageCursor = pageCursor,
                            types = specification.types,
                            includeAll = specification.types.toSet() == NotificationType.ALL.toSet(),
                        )?.determineRelationshipStatus()
                        ?.updatePaginationData()
                        ?.filterNsfw(specification.includeNsfw)
                        .orEmpty()
            }.deduplicate()
        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<NotificationModel>.updatePaginationData(): List<NotificationModel> =
        apply {
            lastOrNull()?.also {
                pageCursor = it.id
            }
            canFetchMore = isNotEmpty()
        }

    private suspend fun List<NotificationModel>.determineRelationshipStatus(): List<NotificationModel> =
        run {
            val userIds = mapNotNull { notification -> notification.user?.id }
            val relationships = userRepository.getRelationships(userIds)
            map { notification ->
                val relationship =
                    relationships?.firstOrNull { rel -> rel.id == notification.user?.id }
                notification.copy(
                    user =
                        notification.user?.copy(
                            relationshipStatus = relationship?.toStatus(),
                            notificationStatus = relationship?.toNotificationStatus(),
                        ),
                )
            }
        }

    private fun List<NotificationModel>.deduplicate(): List<NotificationModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }

    private fun List<NotificationModel>.filterNsfw(included: Boolean): List<NotificationModel> =
        filter { included || it.entry?.isNsfw != true }
}
