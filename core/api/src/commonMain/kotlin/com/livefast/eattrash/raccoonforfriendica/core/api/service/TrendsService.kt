package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface TrendsService {
    @GET("trends/tags")
    suspend fun getHashtags(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): List<Tag>

    @GET("trends/statuses")
    suspend fun getStatuses(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): List<Status>

    @GET("trends/links")
    suspend fun getLinks(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): List<TrendsLink>
}
