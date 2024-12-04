package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal actual class DefaultPushNotificationManager : PushNotificationManager {
    actual override val state: StateFlow<PushNotificationManagerState> =
        MutableStateFlow<PushNotificationManagerState>(PushNotificationManagerState.Unsupported)

    actual override suspend fun getAvailableDistributors(): List<String> = emptyList()

    actual override suspend fun refreshState() {
        // no-op
    }

    actual override suspend fun startup() {
        // no-op
    }

    actual override suspend fun saveDistributor(distributor: String) {
        // no-op
    }

    actual override suspend fun clearDistributor() {
        // no-op
    }

    actual override suspend fun enable() {
        // no-op
    }

    actual override suspend fun disable() {
        // no-op
    }

    actual override suspend fun registerEndpoint(
        account: AccountModel,
        endpoint: String,
    ) {
        // no-op
    }

    actual override suspend fun unregisterEndpoint(account: AccountModel) {
        // no-op
    }
}
