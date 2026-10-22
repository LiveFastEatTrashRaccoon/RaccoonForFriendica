package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent

interface PhotoAlbumService {
    suspend fun update(data: FormDataContent): FriendicaApiResult

    suspend fun delete(data: FormDataContent): FriendicaApiResult
}

fun interface PhotoAlbumServiceFactory {
    fun create(baseUrl: String, client: HttpClient): PhotoAlbumService
}
