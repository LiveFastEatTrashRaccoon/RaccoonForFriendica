package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaApiResult
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaPhoto
import io.ktor.client.request.forms.MultiPartFormDataContent

interface PhotoService {
    suspend fun getAll(): List<FriendicaPhoto>

    suspend fun create(content: MultiPartFormDataContent): FriendicaPhoto

    suspend fun update(content: MultiPartFormDataContent): FriendicaApiResult

    suspend fun delete(content: MultiPartFormDataContent): FriendicaApiResult
}
