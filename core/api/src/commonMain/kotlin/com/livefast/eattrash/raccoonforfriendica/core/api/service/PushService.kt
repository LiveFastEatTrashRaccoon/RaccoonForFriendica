package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PushSubscription
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import io.ktor.client.request.forms.FormDataContent

interface PushService {
    @GET("v1/push/subscription")
    suspend fun get(): PushSubscription?

    @POST("v1/push/subscription")
    suspend fun create(@Body data: FormDataContent): PushSubscription?

    @PUT("v1/push/subscription")
    suspend fun update(@Body data: FormDataContent): PushSubscription?

    @DELETE("v1/push/subscription")
    suspend fun delete()
}
