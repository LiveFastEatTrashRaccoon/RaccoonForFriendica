package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path

interface AnnouncementService {
    @GET("v1/announcements")
    suspend fun getAll(): List<Announcement>

    @POST("v1/announcements/{id}/dismiss")
    suspend fun dismiss(@Path("id") id: String): Response<Unit>

    @PUT("v1/announcements/{id}/reactions/{name}")
    suspend fun addReaction(@Path("id") id: String, @Path("name") name: String): Response<Unit>

    @DELETE("v1/announcements/{id}/reactions/{name}")
    suspend fun removeReaction(@Path("id") id: String, @Path("name") name: String): Response<Unit>
}
