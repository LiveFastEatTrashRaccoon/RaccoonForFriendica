package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface MediaService {
    @GET("v1/media/{id}")
    suspend fun getBy(
        @Path("id") id: String,
    ): MediaAttachment?
}
