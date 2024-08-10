package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import de.jensklingenberg.ktorfit.http.GET
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
}
