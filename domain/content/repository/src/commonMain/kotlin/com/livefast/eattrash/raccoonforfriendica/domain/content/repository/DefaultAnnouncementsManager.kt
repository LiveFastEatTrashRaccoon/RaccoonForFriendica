package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

internal class DefaultAnnouncementsManager(private val announcementRepository: AnnouncementRepository) :
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
