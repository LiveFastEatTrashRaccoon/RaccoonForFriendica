package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhotoAlbum
import io.ktor.client.request.forms.FormDataContent

interface PhotoAlbumService {
    suspend fun getAll(): List<FriendicaPhotoAlbum>

    suspend fun getPhotos(
        album: String,
        maxId: String? = null,
        latestFirst: Boolean = false,
        limit: Int = 20,
    ): List<FriendicaPhoto>

    suspend fun update(data: FormDataContent): FriendicaApiResult

    suspend fun delete(data: FormDataContent): FriendicaApiResult
}
