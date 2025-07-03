package com.livefast.eattrash.raccoonforfriendica.core.api.service

import io.ktor.client.request.forms.FormDataContent

interface ReportService {
    suspend fun create(data: FormDataContent): Boolean
}
