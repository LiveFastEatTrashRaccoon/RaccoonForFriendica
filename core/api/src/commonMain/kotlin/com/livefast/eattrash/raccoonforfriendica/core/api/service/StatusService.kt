package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ScheduledStatus
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Translation
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateStatusForm
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.FormDataContent

interface StatusService {
    @GET("v1/statuses/{id}")
    suspend fun get(
        @Path("id") id: String,
    ): Status

    @GET("v1/statuses/{id}/context")
    suspend fun getContext(
        @Path("id") id: String,
    ): StatusContext

    @GET("v1/statuses/{id}/source")
    suspend fun getSource(
        @Path("id") id: String,
    ): StatusSource

    @POST("v1/statuses/{id}/reblog")
    suspend fun reblog(
        @Path("id") id: String,
        @Body data: FormDataContent,
    ): Status

    @POST("v1/statuses/{id}/unreblog")
    suspend fun unreblog(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/pin")
    suspend fun pin(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/unpin")
    suspend fun unpin(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/bookmark")
    suspend fun bookmark(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/unbookmark")
    suspend fun unbookmark(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/favourite")
    suspend fun favorite(
        @Path("id") id: String,
    ): Status

    @POST("v1/statuses/{id}/unfavourite")
    suspend fun unfavorite(
        @Path("id") id: String,
    ): Status

    @GET("v1/statuses/{id}/favourited_by")
    suspend fun getFavoritedBy(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Account>

    @GET("v1/statuses/{id}/reblogged_by")
    suspend fun getRebloggedBy(
        @Path("id") id: String,
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Account>

    @POST("v1/statuses")
    @Headers("Content-Type: application/json")
    suspend fun create(
        @Header("Idempotency-Key") key: String,
        @Body data: CreateStatusForm,
    ): Status

    @PUT("v1/statuses/{id}")
    @Headers("Content-Type: application/json")
    suspend fun update(
        @Path("id") id: String,
        @Body data: CreateStatusForm,
    ): Status

    @DELETE("v1/statuses/{id}")
    suspend fun delete(
        @Path("id") id: String,
    ): Response<Unit>

    @GET("v1/scheduled_statuses")
    suspend fun getScheduled(
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<ScheduledStatus>

    @GET("v1/scheduled_statuses/{id}")
    suspend fun getScheduledById(
        @Path("id") id: String,
    ): ScheduledStatus

    @PUT("v1/scheduled_statuses/{id}")
    suspend fun updateScheduled(
        @Path("id") id: String,
        @Body data: FormDataContent,
    ): ScheduledStatus

    @DELETE("v1/scheduled_statuses/{id}")
    suspend fun deleteScheduled(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("friendica/activity/dislike")
    suspend fun dislike(
        @Body data: FormDataContent,
    ): Response<Unit>

    @POST("friendica/activity/undislike")
    suspend fun undislike(
        @Body data: FormDataContent,
    ): Response<Unit>

    @POST("v1/statuses/{id}/translate")
    suspend fun translate(
        @Path("id") id: String,
        @Body data: FormDataContent,
    ): Translation
}
