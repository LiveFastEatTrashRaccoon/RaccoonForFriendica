package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess

@AssistedInject
class DefaultAnnouncementService(@Assisted private val args: ServiceCreationArgs) : AnnouncementService {

    override suspend fun getAll(): List<Announcement> =
        args.client.get("${args.baseUrl}/v1/announcements").body<List<Announcement>>()

    override suspend fun dismiss(id: String): Boolean =
        args.client.post("${args.baseUrl}/v1/announcements/$id/dismiss").status.isSuccess()

    override suspend fun addReaction(id: String, name: String): Boolean =
        args.client.post("${args.baseUrl}/v1/announcements/$id/reactions/$name").status.isSuccess()

    override suspend fun removeReaction(id: String, name: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/announcements/$id/reactions/$name").status.isSuccess()
}

@AssistedFactory
fun interface AnnouncementServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultAnnouncementService
}
