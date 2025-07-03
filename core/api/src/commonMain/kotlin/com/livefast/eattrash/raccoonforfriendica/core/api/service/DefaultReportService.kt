package com.livefast.eattrash.raccoonforfriendica.core.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class DefaultReportService(private val baseUrl: String, private val client: HttpClient) : ReportService {
    override suspend fun create(data: FormDataContent) = client.post("$baseUrl/v1/reports") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.status.isSuccess()
}
