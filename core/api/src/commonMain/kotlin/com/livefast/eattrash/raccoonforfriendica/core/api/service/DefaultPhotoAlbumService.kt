package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultPhotoAlbumService(@InjectedParam args: ServiceCreationArgs) : PhotoAlbumService {

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
