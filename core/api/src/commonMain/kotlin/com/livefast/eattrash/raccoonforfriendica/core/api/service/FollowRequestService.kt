package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship

interface FollowRequestService {
    suspend fun getAll(maxId: String? = null, limit: Int = 20): Pair<List<Account>, String?>

    suspend fun accept(id: String): Relationship

    suspend fun reject(id: String): Relationship
}
