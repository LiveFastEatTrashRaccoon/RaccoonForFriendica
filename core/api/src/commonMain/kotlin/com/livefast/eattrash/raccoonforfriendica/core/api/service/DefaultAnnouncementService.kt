package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultAnnouncementService(@InjectedParam args: ServiceCreationArgs) : AnnouncementService {

    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getAll(): List<Announcement> =
        client.get("$baseUrl/v1/announcements").body<List<Announcement>>()

    override suspend fun dismiss(id: String): Boolean =
        client.post("$baseUrl/v1/announcements/$id/dismiss").status.isSuccess()

    override suspend fun addReaction(id: String, name: String): Boolean =
        client.post("$baseUrl/v1/announcements/$id/reactions/$name").status.isSuccess()

    override suspend fun removeReaction(id: String, name: String): Boolean =
        client.delete("$baseUrl/v1/announcements/$id/reactions/$name").status.isSuccess()
}
