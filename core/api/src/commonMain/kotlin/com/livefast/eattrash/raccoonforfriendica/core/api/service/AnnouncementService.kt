package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement

interface AnnouncementService {
    suspend fun getAll(): List<Announcement>

    suspend fun dismiss(id: String): Boolean

    suspend fun addReaction(id: String, name: String): Boolean

    suspend fun removeReaction(id: String, name: String): Boolean
}
