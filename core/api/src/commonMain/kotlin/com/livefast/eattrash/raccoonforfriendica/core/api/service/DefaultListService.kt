package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.FriendicaCircle
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.form.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
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

@AssistedInject
class DefaultListService(@Assisted private val args: ServiceCreationArgs) : ListService {

    override suspend fun getAll(): List<UserList> = args.client.get("${args.baseUrl}/v1/lists").body()

    override suspend fun getFriendicaCircles(): List<FriendicaCircle> =
        args.client.get("${args.baseUrl}/friendica/group_show").body()

    override suspend fun getBy(id: String): UserList = args.client.get("${args.baseUrl}/v1/lists/$id").body()

    override suspend fun getMembers(id: String, maxId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/lists/$id/accounts") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val data: List<Account> = response.body()
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun create(data: EditListForm): UserList = args.client.post("${args.baseUrl}/v1/lists") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun update(id: String, data: EditListForm): UserList =
        args.client.put("${args.baseUrl}/v1/lists/$id") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun delete(id: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/lists/$id").status.isSuccess()

    override suspend fun addMembers(id: String, data: EditListMembersForm): Boolean =
        args.client.post("${args.baseUrl}/v1/lists/$id/accounts") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.status.isSuccess()

    override suspend fun removeMembers(id: String, data: EditListMembersForm): Boolean =
        args.client.delete("${args.baseUrl}/v1/lists/$id/accounts") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.status.isSuccess()
}

@AssistedFactory
fun interface ListServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultListService
}
