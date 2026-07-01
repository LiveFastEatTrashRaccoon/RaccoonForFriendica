package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.serialName
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.parameters
import kotlin.coroutines.cancellation.CancellationException

internal class DefaultPushNotificationRepository(private val provider: ServiceProvider) : PushNotificationRepository {
    override suspend fun create(
        endpoint: String,
        pubKey: String,
        auth: String,
        types: List<NotificationType>,
        policy: NotificationPolicy,
    ): String? = try {
        val data =
            FormDataContent(
                parameters {
                    append("subscription[endpoint]", endpoint)
                    append("subscription[keys][p256dh]", pubKey)
                    append("subscription[keys][auth]", auth)
                    for (type in NotificationType.ALL) {
                        type.toDto()?.also { typeDto ->
                            append(
                                "data[alerts][${typeDto.serialName}]",
                                (type in types).toString(),
                            )
                        }
                    }
                    append("data[policy]", policy.toDto().serialName)
                },
            )
        provider.push.create(data).serverKey
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun update(types: List<NotificationType>, policy: NotificationPolicy): String? = try {
        val data =
            FormDataContent(
                parameters {
                    for (type in NotificationType.ALL) {
                        type.toDto()?.also { typeDto ->
                            append(
                                "data[alerts][${typeDto.serialName}]",
                                (type in types).toString(),
                            )
                        }
                    }
                    append("data[policy]", policy.toDto().serialName)
                },
            )
        provider.push.update(data).serverKey
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun delete(): Boolean = try {
        provider.push.delete()
        true
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        false
    }
}
