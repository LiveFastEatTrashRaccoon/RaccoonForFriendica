package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent

interface MediaService {
    @GET("v1/media/{id}")
    suspend fun getBy(
        @Path("id") id: String,
    ): MediaAttachment?

    @POST("v2/media")
    suspend fun create(
        @Body content: MultiPartFormDataContent,
    ): MediaAttachment

    @PUT("v1/media/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Body content: FormDataContent,
    ): MediaAttachment

    @DELETE("v1/media/{id}")
    suspend fun delete(
        @Path("id") id: String,
    ): Response<Unit>
}
