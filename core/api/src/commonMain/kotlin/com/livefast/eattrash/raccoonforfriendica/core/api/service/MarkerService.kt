package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent

interface MarkerService {
    suspend fun get(timelines: List<String>): Markers

    suspend fun update(data: FormDataContent): Markers
}

fun interface MarkerServiceFactory {
    fun create(baseUrl: String, client: HttpClient): MarkerService
}
