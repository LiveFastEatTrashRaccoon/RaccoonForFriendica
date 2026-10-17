package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted

interface SearchService {
    suspend fun search(
        query: String = "",
        type: String,
        offset: Int? = null,
        following: Boolean = false,
        limit: Int = 20,
        resolve: Boolean = false,
    ): Search
}

fun interface SearchServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): SearchService
}
