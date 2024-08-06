package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface StatusService {
    @GET("statuses/{id}")
    suspend fun get(
        @Path("id") id: String,
    ): Status

    @GET("statuses/{id}/context")
    suspend fun getContext(
        @Path("id") id: String,
    ): StatusContext
}
