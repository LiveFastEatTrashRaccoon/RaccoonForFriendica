package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.PushSubscription
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

@AssistedInject
class DefaultPushService(@Assisted private val args: ServiceCreationArgs) : PushService {

    override suspend fun get(): PushSubscription = args.client.get("${args.baseUrl}/v1/push/subscription").body()

    override suspend fun create(data: FormDataContent): PushSubscription =
        args.client.post("${args.baseUrl}/v1/push/subscription") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun update(data: FormDataContent): PushSubscription =
        args.client.put("${args.baseUrl}/v1/push/subscription") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun delete() = args.client.post("${args.baseUrl}/").status.isSuccess()
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultPushServiceFactory : PushServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultPushService
}
