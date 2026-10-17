package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted

interface AnnouncementService {
    suspend fun getAll(): List<Announcement>

    suspend fun dismiss(id: String): Boolean

    suspend fun addReaction(id: String, name: String): Boolean

    suspend fun removeReaction(id: String, name: String): Boolean
}

fun interface AnnouncementServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): AnnouncementService
}
