package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import io.ktor.client.request.forms.FormDataContent

interface ReportService {
    suspend fun create(data: FormDataContent): Boolean
}

fun interface ReportServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): ReportService
}
