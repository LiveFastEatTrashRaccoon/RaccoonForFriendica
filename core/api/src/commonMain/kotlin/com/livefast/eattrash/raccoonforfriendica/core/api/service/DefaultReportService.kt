package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

@AssistedInject
class DefaultReportService(@Assisted private val args: ServiceCreationArgs) : ReportService {

    override suspend fun create(data: FormDataContent) = args.client.post("${args.baseUrl}/v1/reports") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.status.isSuccess()
}

@AssistedFactory
fun interface ReportServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultReportService
}
