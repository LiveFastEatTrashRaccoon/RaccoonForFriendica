package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status

interface TimelineService {
    suspend fun getPublic(
        maxId: String? = null,
        minId: String? = null,
        limit: Int = 20,
        local: Boolean = false,
    ): List<Status>

    suspend fun getHome(maxId: String? = null, minId: String? = null, limit: Int = 20): List<Status>

    suspend fun getHashtag(
        hashtag: String,
        maxId: String? = null,
        minId: String? = null,
        limit: Int = 20,
    ): Pair<List<Status>, String?>

    suspend fun getList(id: String, maxId: String? = null, minId: String? = null, limit: Int = 20): List<Status>
}
