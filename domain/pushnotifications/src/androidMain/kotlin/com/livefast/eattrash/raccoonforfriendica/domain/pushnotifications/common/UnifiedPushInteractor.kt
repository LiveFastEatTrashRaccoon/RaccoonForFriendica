package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.unifiedpush.android.connector.FailedReason
import org.unifiedpush.android.connector.data.PushEndpoint
import org.unifiedpush.android.connector.data.PushMessage

interface UnifiedPushInteractor {
    fun onMessage(context: Context, message: PushMessage, instance: String)
    fun onNewEndpoint(context: Context, endpoint: PushEndpoint, instance: String)
    fun onRegistrationFailed(context: Context, reason: FailedReason, instance: String)
    fun onUnregistered(context: Context, instance: String)
}

class DefaultUnifiedPushInteractor(
    private val pullNotificationManager: PullNotificationManager,
    private val pushNotificationManager: PushNotificationManager,
    private val accountRepository: AccountRepository,
) : UnifiedPushInteractor {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessage(context: Context, message: PushMessage, instance: String) {
        pullNotificationManager.oneshotCheck()
    }

    override fun onNewEndpoint(context: Context, endpoint: PushEndpoint, instance: String) {
        val accountId = instance.toLongOrNull() ?: return
        scope.launch {
            val account = accountRepository.getBy(accountId) ?: return@launch
            endpoint.pubKeySet
            pushNotificationManager.registerEndpoint(
                account = account,
                endpointUrl = endpoint.url,
                pubKey = endpoint.pubKeySet?.pubKey.orEmpty(),
                auth = endpoint.pubKeySet?.auth.orEmpty(),
            )
        }
    }

    override fun onRegistrationFailed(context: Context, reason: FailedReason, instance: String) {
        val accountId = instance.toLongOrNull() ?: return
        scope.launch {
            val account = accountRepository.getBy(accountId) ?: return@launch
            pushNotificationManager.unregisterEndpoint(account)
        }
    }

    override fun onUnregistered(context: Context, instance: String) {
        val accountId = instance.toLongOrNull() ?: return
        scope.launch {
            val account = accountRepository.getBy(accountId) ?: return@launch
            pushNotificationManager.unregisterEndpoint(account)
        }
    }
}
