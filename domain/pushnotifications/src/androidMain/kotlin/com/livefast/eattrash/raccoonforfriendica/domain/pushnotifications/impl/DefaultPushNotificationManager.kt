package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.impl

import android.app.NotificationManager
import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PushNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.PushNotificationManager
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.PushNotificationManagerState
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.utils.CryptoUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.unifiedpush.android.connector.UnifiedPush

@Single
internal class DefaultPushNotificationManager(
    private val context: Context,
    private val pushNotificationRepository: PushNotificationRepository,
    private val accountRepository: AccountRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PushNotificationManager {
    override val state =
        MutableStateFlow<PushNotificationManagerState>(PushNotificationManagerState.Initializing)

    private val notificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

    override suspend fun getAvailableDistributors(): List<String> =
        withContext(dispatcher) {
            UnifiedPush.getDistributors(context)
        }

    override suspend fun refreshState() {
        val account = accountRepository.getActive() ?: return
        val availableDistributors = getAvailableDistributors()
        if (availableDistributors.isEmpty()) {
            state.update { PushNotificationManagerState.NoDistributors }
            return
        }

        if (account.notificationEnabled) {
            state.update { PushNotificationManagerState.Idle }
            return
        }

        if (availableDistributors.isEmpty()) {
            state.update { PushNotificationManagerState.NoDistributors }
            return
        }

        val selectedDistributor = getSelectedDistributor()
        if (selectedDistributor.isNullOrEmpty()) {
            if (availableDistributors.size == 1) {
                state.update { PushNotificationManagerState.Idle }
            } else {
                state.update { PushNotificationManagerState.NoDistributorSelected }
            }
        }
    }

    override suspend fun startup() {
        val account = accountRepository.getActive() ?: return
        if (account.notificationEnabled) {
            updateSubscription(account)
            state.update { PushNotificationManagerState.Enabled }
            return
        }

        val availableDistributors = getAvailableDistributors()
        if (availableDistributors.isEmpty()) {
            state.update { PushNotificationManagerState.NoDistributors }
            return
        }

        val selectedDistributor = getSelectedDistributor()
        if (!selectedDistributor.isNullOrEmpty()) {
            enable()
            return
        }

        // automatically selects the first one
        if (availableDistributors.size == 1) {
            val firstDistributor = availableDistributors.first()
            saveDistributor(firstDistributor)
            enable()
        }
    }

    override suspend fun saveDistributor(distributor: String) =
        withContext(dispatcher) {
            UnifiedPush.saveDistributor(
                context = context,
                distributor = distributor,
            )
        }

    override suspend fun clearDistributor() =
        withContext(dispatcher) {
            UnifiedPush.safeRemoveDistributor(context)
        }

    override suspend fun enable() {
        val account = accountRepository.getActive() ?: return
        if (account.notificationEnabled) {
            return
        }

        registerForPushNotification(account)
        state.update { PushNotificationManagerState.Enabled }
    }

    override suspend fun disable() {
        val account = accountRepository.getActive() ?: return
        if (!account.notificationEnabled) {
            return
        }

        unregisterForPushNotifications(account)
        state.update { PushNotificationManagerState.Initializing }
    }

    override suspend fun registerEndpoint(
        account: AccountModel,
        endpoint: String,
    ) {
        val keyPair = CryptoUtil.generateECKeyPair()
        val auth = CryptoUtil.secureRandomBytesEncoded(length = 16)
        val types = NotificationType.ALL.filter { it.isEnabled(account) }
        val policy = NotificationPolicy.Followed
        val serverKey =
            pushNotificationRepository.create(
                endpoint = endpoint,
                pubKey = keyPair.pubKey,
                auth = auth,
                types = types,
                policy = policy,
            )
        val updateAccount =
            account.copy(
                pushAuth = auth,
                pushServerKey = serverKey,
                pushPubKey = keyPair.pubKey,
                pushPrivKey = keyPair.privKey,
                unifiedPushUrl = endpoint,
            )
        accountRepository.update(updateAccount)
    }

    override suspend fun unregisterEndpoint(account: AccountModel) {
        pushNotificationRepository.delete()
        val updateAccount =
            account.copy(
                pushAuth = null,
                pushServerKey = null,
                pushPubKey = null,
                pushPrivKey = null,
                unifiedPushUrl = null,
            )
        accountRepository.update(updateAccount)
    }

    private suspend fun getSelectedDistributor(): String? =
        withContext(dispatcher) {
            UnifiedPush.getAckDistributor(context)
        }

    private suspend fun registerForPushNotification(account: AccountModel) =
        withContext(dispatcher) {
            UnifiedPush.registerApp(
                context = context,
                instance = account.channelId,
                features = arrayListOf(UnifiedPush.FEATURE_BYTES_MESSAGE),
            )
        }

    private suspend fun updateSubscription(account: AccountModel) {
        val types = NotificationType.ALL.filter { it.isEnabled(account) }
        val policy = NotificationPolicy.Followed
        val serverKey =
            pushNotificationRepository.update(
                types = types,
                policy = policy,
            )
        val updateAccount = account.copy(pushServerKey = serverKey)
        accountRepository.update(updateAccount)
    }

    private suspend fun unregisterForPushNotifications(account: AccountModel) =
        withContext(dispatcher) {
            UnifiedPush.unregisterApp(
                context = context,
                instance = account.channelId,
            )
        }

    private fun NotificationType.isEnabled(account: AccountModel): Boolean {
        val channelId = getChannelId(account) ?: return false
        val channel = notificationManager.getNotificationChannel(channelId) ?: return false
        return channel.importance > NotificationManager.IMPORTANCE_NONE
    }

    private fun NotificationType.getChannelId(account: AccountModel): String? =
        buildString {
            val typeSegment = channelIdSegment ?: return null
            append(typeSegment)
            append(account.channelId)
        }
}

private val AccountModel.channelId: String get() = id.toString()

private val AccountModel.notificationEnabled: Boolean get() = !unifiedPushUrl.isNullOrEmpty()

private val NotificationType.channelIdSegment: String?
    get() =
        when (this) {
            NotificationType.Entry -> "post"
            NotificationType.Favorite -> "favorite"
            NotificationType.Follow -> "follow"
            NotificationType.FollowRequest -> "follow_request"
            NotificationType.Mention -> "mention"
            NotificationType.Poll -> "poll"
            NotificationType.Reblog -> "reblog"
            NotificationType.Unknown -> "unknown"
            NotificationType.Update -> "update"
            else -> null
        }
