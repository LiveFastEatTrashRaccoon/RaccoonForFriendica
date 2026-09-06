package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Collection
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CollectionItem
import com.livefast.eattrash.raccoonforfriendica.core.api.form.AddCollectionItemForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateCollectionForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

@AssistedInject
class DefaultCollectionService(@Assisted args: ServiceCreationArgs) : CollectionService {
    private val baseUrl = args.baseUrl
    private val client = args.client

    override suspend fun create(data: CreateCollectionForm): Collection = client.post("$baseUrl/v1/collections") {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    override suspend fun get(id: String): Collection = client.get("$baseUrl/v1/collections/$id").body()

    override suspend fun delete(id: String): Boolean = client.delete("$baseUrl/v1/collections/$id").status.isSuccess()

    override suspend fun update(id: String, data: CreateCollectionForm): Collection =
        client.patch("$baseUrl/v1/collections") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun addItem(id: String, data: AddCollectionItemForm): CollectionItem =
        client.post("$baseUrl/v1/collections") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun deleteItem(id: String, itemId: String): Boolean =
        client.delete("$baseUrl/v1/collections/$id/items/$itemId").status.isSuccess()

    override suspend fun revokeInclusion(id: String, itemId: String): Boolean =
        client.delete("$baseUrl/v1/collections/$id/items/$itemId/revoke").status.isSuccess()
}

@AssistedFactory
fun interface CollectionServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultCollectionService
}
