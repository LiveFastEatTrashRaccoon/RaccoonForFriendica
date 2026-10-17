package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted

interface FollowRequestService {
    suspend fun getAll(maxId: String? = null, limit: Int = 20): Page<Account>

    suspend fun accept(id: String): Relationship

    suspend fun reject(id: String): Relationship
}

fun interface FollowRequestServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): FollowRequestService
}
