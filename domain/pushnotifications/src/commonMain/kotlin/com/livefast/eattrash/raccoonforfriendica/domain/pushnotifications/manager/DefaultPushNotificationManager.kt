package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal expect class DefaultPushNotificationManager : PushNotificationManager {
    override val state: StateFlow<PushNotificationManagerState>

    override suspend fun refreshState()

    override suspend fun startup()

    override suspend fun getAvailableDistributors(): List<String>

    override suspend fun saveDistributor(distributor: String)

    override suspend fun clearDistributor()

    override suspend fun enable()

    override suspend fun disable()

    override suspend fun registerEndpoint(
        account: AccountModel,
        endpoint: String,
    )

    override suspend fun unregisterEndpoint(account: AccountModel)
}
