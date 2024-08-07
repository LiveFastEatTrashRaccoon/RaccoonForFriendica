package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.NotificationType
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultCredentialsRepository(
    private val provider: ServiceProvider,
) : CredentialsRepository {
    override suspend fun validate(
        node: String,
        user: String,
        pass: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            provider.changeNode(node)
            runCatching {
                provider.notifications.get(types = listOf(NotificationType.FOLLOW))
                true
            }.getOrElse { false }
        }
}
