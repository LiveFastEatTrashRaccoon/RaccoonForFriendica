package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultPollService(@InjectedParam args: ServiceCreationArgs) : PollService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getById(id: String): Poll = client.get("$baseUrl/v1/polls/$id").body()

    override suspend fun vote(id: String, data: SubmitPollVoteForm): Poll = client.post("$baseUrl/v1/polls/$id/votes") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()
}
