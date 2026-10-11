package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultPhotoService(@Assisted private val args: ServiceCreationArgs) : PhotoService {

    override suspend fun getAll(): List<FriendicaPhoto> =
        args.client.get("${args.baseUrl}/friendica/photos/list").body()

    override suspend fun create(content: MultiPartFormDataContent): FriendicaPhoto =
        args.client.post("${args.baseUrl}/friendica/photo/create") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun update(content: MultiPartFormDataContent): FriendicaApiResult =
        args.client.post("${args.baseUrl}/friendica/photo/update") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun delete(content: MultiPartFormDataContent): FriendicaApiResult =
        args.client.post("${args.baseUrl}/friendica/photo/delete") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()
}

@AssistedFactory
fun interface PhotoServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultPhotoService
}
