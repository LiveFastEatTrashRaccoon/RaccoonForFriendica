package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

@AssistedInject
class DefaultMediaService(@Assisted args: ServiceCreationArgs) : MediaService {
    private val baseUrl = args.baseUrl
    private val client = args.client
    override suspend fun getBy(id: String): MediaAttachment = client.get("$baseUrl/v1/media/$id").body()

    override suspend fun create(content: MultiPartFormDataContent): MediaAttachment = client.post("$baseUrl/v2/media") {
        contentType(ContentType.Application.Json)
        setBody(content)
    }.body()

    override suspend fun update(id: String, content: FormDataContent): MediaAttachment =
        client.put("$baseUrl/v1/media/$id") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun delete(id: String): Boolean = client.delete("$baseUrl/v1/media/$id").status.isSuccess()
}

@AssistedFactory
fun interface MediaServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultMediaService
}
