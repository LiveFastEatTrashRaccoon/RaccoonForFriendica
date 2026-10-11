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
class DefaultCollectionService(@Assisted private val args: ServiceCreationArgs) : CollectionService {

    override suspend fun create(data: CreateCollectionForm): Collection =
        args.client.post("${args.baseUrl}/v1/collections") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun get(id: String): Collection = args.client.get("${args.baseUrl}/v1/collections/$id").body()

    override suspend fun delete(id: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/collections/$id").status.isSuccess()

    override suspend fun update(id: String, data: CreateCollectionForm): Collection =
        args.client.patch("${args.baseUrl}/v1/collections") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun addItem(id: String, data: AddCollectionItemForm): CollectionItem =
        args.client.post("${args.baseUrl}/v1/collections") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun deleteItem(id: String, itemId: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/collections/$id/items/$itemId").status.isSuccess()

    override suspend fun revokeInclusion(id: String, itemId: String): Boolean =
        args.client.delete("${args.baseUrl}/v1/collections/$id/items/$itemId/revoke").status.isSuccess()
}

@AssistedFactory
fun interface CollectionServiceFactory {
    fun create(@Assisted args: ServiceCreationArgs): DefaultCollectionService
}
