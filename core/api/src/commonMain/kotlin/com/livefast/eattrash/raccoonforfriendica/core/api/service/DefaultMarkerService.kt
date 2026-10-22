package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultMarkerService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    MarkerService {

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

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultMarkerServiceFactory : MarkerServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultMarkerService
}
