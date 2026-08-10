package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultFollowRequestService(@InjectedParam args: ServiceCreationArgs) : FollowRequestService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun getAll(maxId: String?, limit: Int): Pair<List<Account>, String?> {
        val response = client.get("$baseUrl/v1/follow_requests") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Account> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return data to cursor
    }

    override suspend fun accept(id: String): Relationship =
        client.post("$baseUrl/v1/follow_requests/$id/authorize").body()

    override suspend fun reject(id: String): Relationship = client.post("$baseUrl/v1/follow_requests/$id/reject").body()
}
