package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search

interface SearchService {
    suspend fun search(
        query: String = "",
        type: String,
        maxId: String? = null,
        minId: String? = null,
        following: Boolean = false,
        limit: Int = 20,
        resolve: Boolean = false,
    ): Search
}
