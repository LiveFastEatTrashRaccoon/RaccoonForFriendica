package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface TagsService {
    @GET("v1/followed_tags")
    suspend fun getFollowedTags(): List<Tag>

    @POST("v1/tags/{id}/follow")
    @Headers("Content-Type: application/json")
    suspend fun follow(
        @Path("id") name: String,
    ): Tag?

    @POST("v1/tags/{id}/unfollow")
    @Headers("Content-Type: application/json")
    suspend fun unfollow(
        @Path("id") name: String): Tag?

    @GET("v1/tags/{id}")
    suspend fun get(
        @Path("id") name: String,
    ): Tag?
}
