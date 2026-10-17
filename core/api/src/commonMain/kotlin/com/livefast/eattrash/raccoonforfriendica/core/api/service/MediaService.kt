package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MediaAttachment
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent

interface MediaService {
    suspend fun getBy(id: String): MediaAttachment

    suspend fun create(content: MultiPartFormDataContent): MediaAttachment

    suspend fun update(id: String, content: FormDataContent): MediaAttachment

    suspend fun delete(id: String): Boolean
}

fun interface MediaServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): MediaService
}
