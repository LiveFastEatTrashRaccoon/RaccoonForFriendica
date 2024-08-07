package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Notification
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface NotificationService {
    @GET("notifications")
    suspend fun get(
        @Query("types") types: List<NotificationType>,
        @Query("max_id") maxId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20,
    ): List<Notification>
}
