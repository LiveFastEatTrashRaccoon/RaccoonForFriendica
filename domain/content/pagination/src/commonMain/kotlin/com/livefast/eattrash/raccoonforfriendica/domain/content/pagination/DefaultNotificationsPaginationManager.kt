package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository

internal class DefaultNotificationsPaginationManager(
    private val notificationRepository: NotificationRepository,
) : NotificationsPaginationManager {
    private var specification: NotificationsPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<NotificationModel>()

    override suspend fun reset(specification: NotificationsPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
    }

    override suspend fun loadNextPage(): List<NotificationModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is NotificationsPaginationSpecification.Default ->
                    notificationRepository.getAll(
                        pageCursor = pageCursor,
                        types = specification.types,
                    )
            }.deduplicate()

        if (results.isNotEmpty()) {
            pageCursor = results.last().id
            canFetchMore = true
        } else {
            canFetchMore = false
        }

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<NotificationModel>.deduplicate(): List<NotificationModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }
}
