package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Announcement
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.isSuccess

internal class DefaultAnnouncementService(private val baseUrl: String, private val client: HttpClient) :
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
