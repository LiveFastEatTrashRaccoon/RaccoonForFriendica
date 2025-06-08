package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AnnouncementModel

interface AnnouncementRepository {
    suspend fun getAll(refresh: Boolean = false): List<AnnouncementModel>?

    suspend fun markAsRead(id: String): Boolean

    suspend fun addReaction(id: String, reaction: String): Boolean

    suspend fun removeReaction(id: String, reaction: String): Boolean
}
