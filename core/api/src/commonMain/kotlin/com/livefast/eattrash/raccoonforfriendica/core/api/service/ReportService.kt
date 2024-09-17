package com.livefast.eattrash.raccoonforfriendica.core.api.service

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.request.forms.FormDataContent

interface ReportService {
    @POST("v1/reports")
    suspend fun create(
        @Body data: FormDataContent,
    )
}
