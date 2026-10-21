package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess

@AssistedInject
class DefaultAnnouncementService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    AnnouncementService {

    override suspend fun getAll(): List<Announcement> =
        client.get("$baseUrl/v1/announcements").body<List<Announcement>>()

    override suspend fun dismiss(id: String): Boolean =
        client.post("$baseUrl/v1/announcements/$id/dismiss").status.isSuccess()

    override suspend fun addReaction(id: String, name: String): Boolean =
        client.post("$baseUrl/v1/announcements/$id/reactions/$name").status.isSuccess()

    override suspend fun removeReaction(id: String, name: String): Boolean =
        client.delete("$baseUrl/v1/announcements/$id/reactions/$name").status.isSuccess()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultAnnouncementServiceFactory : AnnouncementServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultAnnouncementService
}
