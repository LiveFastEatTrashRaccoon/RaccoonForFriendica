package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface FollowRequestService {
    @GET("v1/follow_requests")
    suspend fun getAll(
        @Query("max_id") maxId: String? = null,
        @Query("limit") limit: Int = 20,
    ): Response<List<Account>>

    @POST("v1/follow_requests/{id}/authorize")
    @Headers("Content-Type: application/json")
    suspend fun accept(
        @Path("id") id: String,
    ): Relationship

    @POST("v1/follow_requests/{id}/reject")
    @Headers("Content-Type: application/json")
    suspend fun reject(
        @Path("id") id: String,
    ): Relationship
}
