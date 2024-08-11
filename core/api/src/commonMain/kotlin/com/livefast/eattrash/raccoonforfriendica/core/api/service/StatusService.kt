package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.form.ReblogPostForm
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface StatusService {
    @GET("v1/statuses/{id}")
    suspend fun get(
        @Path("id") id: String,
    ): Status

    @GET("v1/statuses/{id}/context")
    suspend fun getContext(
        @Path("id") id: String,
    ): StatusContext

    @POST("v1/statuses/{id}/reblog")
    @Headers("Content-Type: application/json")
    suspend fun reblog(
        @Path("id") id: String,
        @Body data: ReblogPostForm,
    ): Status

    @POST("v1/statuses/{id}/unreblog")
    @Headers("Content-Type: application/json")
    suspend fun unreblog(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/pin")
    @Headers("Content-Type: application/json")
    suspend fun pin(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/unpin")
    @Headers("Content-Type: application/json")
    suspend fun unpin(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/bookmark")
    @Headers("Content-Type: application/json")
    suspend fun bookmark(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/unbookmark")
    @Headers("Content-Type: application/json")
    suspend fun unbookmark(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/favourite")
    @Headers("Content-Type: application/json")
    suspend fun favorite(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/unfavourite")
    @Headers("Content-Type: application/json")
    suspend fun unfavorite(
        @Path("id") id: String,
    ): Status
}
