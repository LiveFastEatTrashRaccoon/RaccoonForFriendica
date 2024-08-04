package com.github.akesiseli.raccoonforfriendica.core.api.service

import com.github.akesiseli.raccoonforfriendica.core.api.dto.Status
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface TimelineService {
    @GET("timelines/public")
    suspend fun getPublic(
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Status>

    @GET("timelines/home")
    suspend fun getHome(
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Status>

    @GET("timelines/tag/{hashtag}")
    suspend fun getHashtag(
        @Path("hashtag") hashtag: String,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Status>
}
