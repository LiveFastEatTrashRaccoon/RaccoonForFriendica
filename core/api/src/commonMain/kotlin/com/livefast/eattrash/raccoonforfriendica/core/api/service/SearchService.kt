package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface SearchService {
    @GET("v2/search")
    suspend fun search(
        @Query("q") query: String = "",
        @Query("type") type: String,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("following") following: Boolean = false,
        @Query("limit") limit: Int = 20,
    ): Search
}
