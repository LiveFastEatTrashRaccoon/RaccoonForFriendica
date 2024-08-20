package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface AppService {
    @POST("v1/apps")
    @Headers("Content-Type: application/json")
    suspend fun create(
        @Body data: CreateAppForm,
    ): Application

    @GET("v1/apps/verify_credentials")
    suspend fun verifyCredentials(): Application
}
