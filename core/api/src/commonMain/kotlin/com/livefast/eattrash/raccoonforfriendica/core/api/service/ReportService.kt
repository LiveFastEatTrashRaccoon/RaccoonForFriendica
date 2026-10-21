package com.livefast.eattrash.raccoonforfriendica.core.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent

interface ReportService {
    suspend fun create(data: FormDataContent): Boolean
}

fun interface ReportServiceFactory {
    fun create(baseUrl: String, client: HttpClient): ReportService
}
