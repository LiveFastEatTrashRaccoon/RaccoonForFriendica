package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

@AssistedInject
class DefaultFollowRequestService(@Assisted private val baseUrl: String, @Assisted private val client: HttpClient) :
    FollowRequestService {

    override suspend fun getAll(maxId: String?, limit: Int): Page<Account> {
        val response = client.get("$baseUrl/v1/follow_requests") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Account> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun accept(id: String): Relationship =
        client.post("$baseUrl/v1/follow_requests/$id/authorize").body()

    override suspend fun reject(id: String): Relationship = client.post("$baseUrl/v1/follow_requests/$id/reject").body()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultFollowRequestServiceFactory : FollowRequestServiceFactory {
    override fun create(@Assisted baseUrl: String, @Assisted client: HttpClient): DefaultFollowRequestService
}
