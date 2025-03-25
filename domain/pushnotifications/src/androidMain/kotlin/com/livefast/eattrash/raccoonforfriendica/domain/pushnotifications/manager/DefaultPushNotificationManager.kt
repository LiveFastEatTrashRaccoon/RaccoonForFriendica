package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager

import android.app.NotificationManager
import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PushNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.unifiedpush.android.connector.UnifiedPush

internal class DefaultPushNotificationManager(
    private val context: Context,
    private val pushNotificationRepository: PushNotificationRepository,
    private val accountRepository: AccountRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PushNotificationManager {
    private val _state =
        MutableStateFlow<PushNotificationManagerState>(PushNotificationManagerState.Initializing)

    override val state: StateFlow<PushNotificationManagerState> = _state

    private val notificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

    override suspend fun getAvailableDistributors(): List<String> =
        withContext(dispatcher) {
            UnifiedPush.getDistributors(context)
        }

    override suspend fun refreshState() {
        val account = accountRepository.getActive() ?: return
        val availableDistributors = getAvailableDistributors()
        check(availableDistributors.isNotEmpty()) {
            _state.update { PushNotificationManagerState.NoDistributors }
            return
        }

        check(!account.notificationEnabled) {
            _state.update { PushNotificationManagerState.Idle }
            return
        }

        val selectedDistributor = getSelectedDistributor()
        if (selectedDistributor.isNullOrEmpty()) {
            if (availableDistributors.size == 1) {
                _state.update { PushNotificationManagerState.Idle }
            } else {
                _state.update { PushNotificationManagerState.NoDistributorSelected }
            }
        }
    }

    override suspend fun startup() {
        val account = accountRepository.getActive() ?: return
        check(!account.notificationEnabled) {
            updateSubscription(account)
            _state.update { PushNotificationManagerState.Enabled }
            return
        }

        val availableDistributors = getAvailableDistributors()
        check(availableDistributors.isNotEmpty()) {
            _state.update { PushNotificationManagerState.NoDistributors }
            return
        }

        val selectedDistributor = getSelectedDistributor()
        check(selectedDistributor.isNullOrEmpty()) {
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
            UnifiedPush.removeDistributor(context)
        }

    override suspend fun enable() {
        val account = accountRepository.getActive() ?: return
        check(!account.notificationEnabled) { return }

        registerForPushNotification(account)
        _state.update { PushNotificationManagerState.Enabled }
    }

    override suspend fun disable() {
        val account = accountRepository.getActive() ?: return
        check(account.notificationEnabled) { return }

        unregisterForPushNotifications(account)
        _state.update { PushNotificationManagerState.Initializing }
    }

    override suspend fun registerEndpoint(
        account: AccountModel,
        endpointUrl: String,
        pubKey: String,
        auth: String,
    ) {
        val types = NotificationType.ALL.filter { it.isEnabled(account) }
        val policy = NotificationPolicy.Followed
        val serverKey =
            pushNotificationRepository.create(
                endpoint = endpointUrl,
                pubKey = pubKey,
                auth = auth,
                types = types,
                policy = policy,
            )
        val updateAccount =
            account.copy(
                pushAuth = auth,
                pushServerKey = serverKey,
                pushPubKey = pubKey,
                pushPrivKey = "",
                unifiedPushUrl = endpointUrl,
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
            UnifiedPush.register(
                context = context,
                instance = account.channelId,
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
            UnifiedPush.unregister(
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
