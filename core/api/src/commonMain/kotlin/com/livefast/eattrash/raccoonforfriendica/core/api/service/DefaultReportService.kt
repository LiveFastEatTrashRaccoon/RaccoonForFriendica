package com.livefast.eattrash.raccoonforfriendica.core.api.service

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

@AssistedInject
class DefaultReportService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    ReportService {

    override suspend fun create(data: FormDataContent) = client.post("$baseUrl/v1/reports") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.status.isSuccess()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultReportServiceFactory : ReportServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultReportService
}
