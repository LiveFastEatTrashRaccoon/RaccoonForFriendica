package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.hasLaterIdThan
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultInboxManager(
    private val notificationRepository: NotificationRepository,
    private val markerRepository: MarkerRepository,
) : InboxManager {
    override val unreadCount = MutableStateFlow(0)

    override suspend fun clearUnreadCount() {
        unreadCount.update { 0 }
    }

    override suspend fun refreshUnreadCount() {
        val lastReadId = markerRepository.get(MarkerType.Notifications)?.lastReadId
        val response = notificationRepository.getAll(refresh = true)
        unreadCount.update {
            response?.list?.count { it.hasLaterIdThan(lastReadId) } ?: 0
        }
    }

    override suspend fun decrementUnreadCount() {
        unreadCount.getAndUpdate {
            (it - 1).coerceAtLeast(0)
        }
    }
}
