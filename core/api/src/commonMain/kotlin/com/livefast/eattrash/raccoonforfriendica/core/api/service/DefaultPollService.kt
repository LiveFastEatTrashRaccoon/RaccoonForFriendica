package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.form.SubmitPollVoteForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultPollService(@Assisted private val args: ServiceCreationArgs) : PollService {

    override suspend fun getById(id: String): Poll = args.client.get("${args.baseUrl}/v1/polls/$id").body()

    override suspend fun vote(id: String, data: SubmitPollVoteForm): Poll =
        args.client.post("${args.baseUrl}/v1/polls/$id/votes") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultPollServiceFactory : PollServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultPollService
}
