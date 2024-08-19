package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.request.forms.MultiPartFormDataContent

interface PhotoService {
    @POST("friendica/photo/create")
    @Headers("Content-Type: application/json")
    suspend fun create(
        @Body content: MultiPartFormDataContent,
    ): FriendicaPhoto

    @POST("friendica/photo/update")
    @Headers("Content-Type: application/json")
    suspend fun update(
        @Body content: MultiPartFormDataContent,
    )

    @POST("friendica/photo/delete")
    suspend fun delete(
        @Body content: MultiPartFormDataContent,
    )
}
