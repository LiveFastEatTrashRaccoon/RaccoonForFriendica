package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultPhotoAlbumService(@Assisted args: ServiceCreationArgs) : PhotoAlbumService {

    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun update(data: FormDataContent): FriendicaApiResult =
        client.post("$baseUrl/friendica/photoalbum/update") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun delete(data: FormDataContent): FriendicaApiResult =
        client.post("$baseUrl/friendica/photoalbum/delete") {
            setBody(data)
        }.body()
}

@AssistedFactory
fun interface PhotoAlbumServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultPhotoAlbumService
}
