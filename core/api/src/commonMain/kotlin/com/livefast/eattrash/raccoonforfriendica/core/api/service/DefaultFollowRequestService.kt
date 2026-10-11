package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

@AssistedInject
class DefaultFollowRequestService(@Assisted private val args: ServiceCreationArgs) : FollowRequestService {

    override suspend fun getAll(maxId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/follow_requests") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Account> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun accept(id: String): Relationship =
        args.client.post("${args.baseUrl}/v1/follow_requests/$id/authorize").body()

    override suspend fun reject(id: String): Relationship =
        args.client.post("${args.baseUrl}/v1/follow_requests/$id/reject").body()
}

@AssistedFactory
fun interface FollowRequestServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultFollowRequestService
}
