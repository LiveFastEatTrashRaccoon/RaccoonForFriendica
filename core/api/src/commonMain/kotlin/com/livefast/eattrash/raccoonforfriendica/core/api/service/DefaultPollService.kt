package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class DefaultPollService(private val baseUrl: String, private val client: HttpClient) : PollService {
    override suspend fun getById(id: String): Poll = client.get("$baseUrl/v1/polls/$id").body()

    override suspend fun vote(id: String, data: SubmitPollVoteForm): Poll = client.post("$baseUrl/v1/polls/$id/votes") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()
}
