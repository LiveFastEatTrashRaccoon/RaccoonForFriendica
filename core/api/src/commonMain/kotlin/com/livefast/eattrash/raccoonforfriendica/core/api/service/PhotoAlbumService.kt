package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import io.ktor.client.request.forms.FormDataContent

interface PhotoAlbumService {
    suspend fun update(data: FormDataContent): FriendicaApiResult

    suspend fun delete(data: FormDataContent): FriendicaApiResult
}

fun interface PhotoAlbumServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): PhotoAlbumService
}
