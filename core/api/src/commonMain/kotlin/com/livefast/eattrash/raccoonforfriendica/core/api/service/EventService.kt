package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface EventService {
    @GET("friendica/events")
    suspend fun getAll(
        @Query("since_id") maxId: Long? = null,
        @Query("count") count: Int,
    ): List<Event>
}
