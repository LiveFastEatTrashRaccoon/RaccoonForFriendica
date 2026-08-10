package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
internal class DefaultListService(@InjectedParam args: ServiceCreationArgs) : ListService {
    private val baseUrl = args.baseUrl
    private val client = args.client
    override suspend fun getAll(): List<UserList> = client.get("$baseUrl/v1/lists").body()

    override suspend fun getFriendicaCircles(): List<FriendicaCircle> =
        client.get("$baseUrl/friendica/group_show").body()

    override suspend fun getBy(id: String): UserList = client.get("$baseUrl/v1/lists/$id").body()

    override suspend fun getMembers(id: String, maxId: String?, limit: Int): List<Account> =
        client.get("$baseUrl/v1/lists/$id/accounts") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }.body()

    override suspend fun create(data: EditListForm): UserList = client.post("$baseUrl/v1/lists") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun update(id: String, data: EditListForm): UserList = client.put("$baseUrl/v1/lists/$id") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun delete(id: String): Boolean = client.delete("$baseUrl/v1/lists/$id").status.isSuccess()

    override suspend fun addMembers(id: String, data: EditListMembersForm): Boolean =
        client.post("$baseUrl/v1/lists/$id/accounts") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.status.isSuccess()

    override suspend fun removeMembers(id: String, data: EditListMembersForm): Boolean =
        client.delete("$baseUrl/v1/lists/$id/accounts") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.status.isSuccess()
}
