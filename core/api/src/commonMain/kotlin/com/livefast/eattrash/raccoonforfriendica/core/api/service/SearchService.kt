package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import io.ktor.client.HttpClient

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
    fun create(baseUrl: String, client: HttpClient): SearchService
}
