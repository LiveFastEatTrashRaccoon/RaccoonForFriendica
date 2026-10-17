package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import io.ktor.client.request.forms.FormDataContent

interface MarkerService {
    suspend fun get(timelines: List<String>): Markers

    suspend fun update(data: FormDataContent): Markers
}

fun interface MarkerServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): MarkerService
}
