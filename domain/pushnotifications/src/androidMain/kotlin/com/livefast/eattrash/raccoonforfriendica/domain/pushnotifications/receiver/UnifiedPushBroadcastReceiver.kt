package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.receiver

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.PushNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import org.unifiedpush.android.connector.MessagingReceiver

class UnifiedPushBroadcastReceiver : MessagingReceiver() {
    private val pullNotificationManager
        by inject<PullNotificationManager>(PullNotificationManager::class.java)
    private val pushNotificationManager
        by inject<PushNotificationManager>(PushNotificationManager::class.java)
    private val accountRepository by inject<AccountRepository>(AccountRepository::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessage(
        context: Context,
        message: ByteArray,
        instance: String,
    ) {
        pullNotificationManager.oneshotCheck()
    }

    override fun onNewEndpoint(
        context: Context,
        endpoint: String,
        instance: String,
    ) {
        val accountId = instance.toLongOrNull() ?: return
        scope.launch {
            val account = accountRepository.getBy(accountId) ?: return@launch
            pushNotificationManager.registerEndpoint(
                account = account,
                endpoint = endpoint,
            )
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
