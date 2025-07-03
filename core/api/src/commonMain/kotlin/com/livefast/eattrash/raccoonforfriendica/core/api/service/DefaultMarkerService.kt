package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class DefaultMarkerService(private val baseUrl: String, private val client: HttpClient) : MarkerService {
    override suspend fun get(timelines: List<String>): Markers = client.get("$baseUrl/v1/markers") {
        timelines.forEach { value ->
            parameter("timeline[]", value)
        }
    }.body()

    override suspend fun update(data: FormDataContent): Markers = client.post("$baseUrl/v1/markers") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()
}
