package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

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
class DefaultAnnouncementsManager(private val announcementRepository: AnnouncementRepository) :
    AnnouncementsManager {
    override val unreadCount = MutableStateFlow(0)

    override suspend fun clearUnreadCount() {
        unreadCount.update { 0 }
    }

    override suspend fun refreshUnreadCount() {
        val announcements = announcementRepository.getAll(refresh = true)
        unreadCount.update {
            announcements?.count { !it.read } ?: 0
        }
    }

    override suspend fun decrementUnreadCount() {
        unreadCount.getAndUpdate {
            (it - 1).coerceAtLeast(0)
        }
    }
}
