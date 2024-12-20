package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DefaultPushNotificationManager : PushNotificationManager {
    override val state: StateFlow<PushNotificationManagerState> =
        MutableStateFlow<PushNotificationManagerState>(PushNotificationManagerState.Unsupported)

    override suspend fun getAvailableDistributors(): List<String> = emptyList()

    override suspend fun refreshState() {
        // no-op
    }

    override suspend fun startup() {
        // no-op
    }

    override suspend fun saveDistributor(distributor: String) {
        // no-op
    }

    override suspend fun clearDistributor() {
        // no-op
    }

    override suspend fun enable() {
        // no-op
    }

    override suspend fun disable() {
        // no-op
    }

    override suspend fun registerEndpoint(
        account: AccountModel,
        endpoint: String,
    ) {
        // no-op
    }

    override suspend fun unregisterEndpoint(account: AccountModel) {
        // no-op
    }
}
