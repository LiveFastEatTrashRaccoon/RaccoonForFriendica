package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.receiver

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager.PushNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.kodein.di.instance
import org.unifiedpush.android.connector.FailedReason
import org.unifiedpush.android.connector.MessagingReceiver
import org.unifiedpush.android.connector.data.PushEndpoint
import org.unifiedpush.android.connector.data.PushMessage

class UnifiedPushBroadcastReceiver : MessagingReceiver() {
    private val pullNotificationManager by RootDI.di.instance<PullNotificationManager>()
    private val pushNotificationManager by RootDI.di.instance<PushNotificationManager>()
    private val accountRepository by RootDI.di.instance<AccountRepository>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessage(
        context: Context,
        message: PushMessage,
        instance: String) {
        pullNotificationManager.oneshotCheck()
    }

    override fun onNewEndpoint(
        context: Context,
        endpoint: PushEndpoint, instance: String) {
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

    override fun onRegistrationFailed(
        context: Context,
        reason: FailedReason, instance: String) {
        val accountId = instance.toLongOrNull() ?: return
        scope.launch {
            val account = accountRepository.getBy(accountId) ?: return@launch
            pushNotificationManager.unregisterEndpoint(account)
        }
    }

    override fun onUnregistered(
        context: Context,
        instance: String,
    ) {
        val accountId = instance.toLongOrNull() ?: return
        scope.launch {
            val account = accountRepository.getBy(accountId) ?: return@launch
            pushNotificationManager.unregisterEndpoint(account)
        }
    }
}
