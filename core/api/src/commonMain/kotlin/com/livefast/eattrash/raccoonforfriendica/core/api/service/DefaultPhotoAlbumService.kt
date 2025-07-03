package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhotoAlbum
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class DefaultPhotoAlbumService(private val baseUrl: String, private val client: HttpClient) :
    PhotoAlbumService {
    override suspend fun getAll(): List<FriendicaPhotoAlbum> = client.post("$baseUrl/friendica/photoalbums").body()

    override suspend fun getPhotos(
        album: String,
        maxId: String?,
        latestFirst: Boolean,
        limit: Int,
    ): List<FriendicaPhoto> = client.post("$baseUrl/friendica/photoalbum") {
        parameter("album", album)
        parameter("maxId", maxId)
        parameter("latest_first", latestFirst)
        parameter("limit", limit)
    }.body()

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
