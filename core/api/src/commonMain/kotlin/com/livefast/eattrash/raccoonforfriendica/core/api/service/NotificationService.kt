package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface NotificationService {
    @GET("v1/notifications")
    suspend fun get(
        @Query("types") types: List<NotificationType>,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("include_all") includeAll: Boolean = false,
        @Query("limit") limit: Int = 20,
    ): List<Notification>

    @POST("v1/notifications/{id}/dismiss")
    suspend fun dismiss(
        @Path("id") id: String,
    )

    @POST("v1/notifications/clear")
    suspend fun dismissAll()
}
