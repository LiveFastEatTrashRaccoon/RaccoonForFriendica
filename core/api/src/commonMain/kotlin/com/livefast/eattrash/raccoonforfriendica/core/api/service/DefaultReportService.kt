package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultReportService(@InjectedParam args: ServiceCreationArgs) : ReportService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun create(data: FormDataContent) = client.post("$baseUrl/v1/reports") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.status.isSuccess()
}
