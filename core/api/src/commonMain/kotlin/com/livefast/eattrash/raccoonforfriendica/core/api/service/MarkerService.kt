package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.FormDataContent

interface MarkerService {
    @GET("v1/markers")
    suspend fun get(@Query("timeline[]") timelines: List<String>): Markers

    @POST("v1/markers")
    suspend fun update(@Body data: FormDataContent): Markers
}
