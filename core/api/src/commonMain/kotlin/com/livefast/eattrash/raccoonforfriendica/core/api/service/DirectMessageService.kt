package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPrivateMessage
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.FormDataContent

interface DirectMessageService {
    @GET("api/direct_messages")
    suspend fun getAll(
        @Query("count") count: Int,
        @Query("page") page: Int,
        @Query("max_id") maxId: Long? = null,
        @Query("getText") getText: String = "html",
    ): List<FriendicaPrivateMessage>

    @GET("api/direct_messages/conversation")
    suspend fun getConversation(
        @Query("uri") uri: String,
        @Query("count") count: Int,
        @Query("page") page: Int,
        @Query("max_id") maxId: Long? = null,
        @Query("getText") getText: String = "html",
    ): List<FriendicaPrivateMessage>

    @POST("api/direct_messages/new")
    suspend fun create(
        @Body data: FormDataContent,
    ): FriendicaApiResult

    @POST("api/direct_messages/destroy")
    @Headers("Content-Type: application/json")
    suspend fun delete(
        @Query("id") id: Long,
    ): FriendicaApiResult

    @GET("api/friendica/direct_messages_setseen")
    suspend fun markAsRead(
        @Query("id") id: Long,
    ): FriendicaApiResult
}
