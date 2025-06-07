package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.request.forms.MultiPartFormDataContent

interface PhotoService {
    @POST("friendica/photo/create")
    suspend fun create(@Body content: MultiPartFormDataContent): FriendicaPhoto

    @POST("friendica/photo/update")
    suspend fun update(@Body content: MultiPartFormDataContent): FriendicaApiResult

    @POST("friendica/photo/delete")
    suspend fun delete(@Body content: MultiPartFormDataContent): FriendicaApiResult
}
