package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultPollService(@Assisted args: ServiceCreationArgs) : PollService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getById(id: String): Poll = client.get("$baseUrl/v1/polls/$id").body()

    override suspend fun vote(id: String, data: SubmitPollVoteForm): Poll = client.post("$baseUrl/v1/polls/$id/votes") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()
}

@AssistedFactory
fun interface PollServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultPollService
}
