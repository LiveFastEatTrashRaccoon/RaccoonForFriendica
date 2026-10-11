package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultMarkerService(@Assisted private val args: ServiceCreationArgs) : MarkerService {

    override suspend fun get(timelines: List<String>): Markers = args.client.get("${args.baseUrl}/v1/markers") {
        timelines.forEach { value ->
            parameter("timeline[]", value)
        }
    }.body()

    override suspend fun update(data: FormDataContent): Markers = args.client.post("${args.baseUrl}/v1/markers") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()
}

@AssistedFactory
fun interface MarkerServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultMarkerService
}
