package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.LogFactory
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NotificationType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PushNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.unifiedpush.android.connector.UnifiedPush

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultPushNotificationManager(
    private val context: Context,
    private val pushNotificationRepository: PushNotificationRepository,
    private val nodeInfoRepository: NodeInfoRepository,
    private val accountRepository: AccountRepository,
    logFactory: LogFactory,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PushNotificationManager {

    override val state: StateFlow<PushNotificationManagerState> field = MutableStateFlow<PushNotificationManagerState>(
        PushNotificationManagerState.Initializing,
    )

    private val log = logFactory.create("DefaultPushNotificationManager")

    private val notificationManager by lazy { context.getSystemService(NotificationManager::class.java) }

    override suspend fun getAvailableDistributors(): List<String> = withContext(dispatcher) {
        UnifiedPush.getDistributors(context)
    }

    override suspend fun refreshState() {
        log.d { "refreshState" }
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
        log.d { "startup" }
        val account = accountRepository.getActive() ?: return
        createNotificationChannelsIfNeeded(account)

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

    override suspend fun saveDistributor(distributor: String) = withContext(dispatcher) {
        log.d { "saveDistributor" }
        UnifiedPush.saveDistributor(
            context = context,
            distributor = distributor,
        )
    }

    override suspend fun clearDistributor() = withContext(dispatcher) {
        log.d { "clearDistributor" }
        UnifiedPush.removeDistributor(context)
    }

    override suspend fun enable() {
        log.d { "enable" }
        val account = accountRepository.getActive() ?: return
        if (account.notificationEnabled) {
            return
        }

        registerForPushNotification(account)
        state.update { PushNotificationManagerState.Enabled }
    }

    override suspend fun disable() {
        log.d { "disable" }
        val account = accountRepository.getActive() ?: return
        if (!account.notificationEnabled) {
            return
        }

        unregisterForPushNotifications(account)
        state.update { PushNotificationManagerState.Initializing }
    }

    override suspend fun registerEndpoint(account: AccountModel, endpointUrl: String, pubKey: String, auth: String) {
        log.d { "registerEndpoint" }
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
        log.d { "unregisterEndpoint" }
        pushNotificationRepository.delete()
        val updateAccount =
            account.copy(
                pushAuth = null,
                pushServerKey = null,
                pushPubKey = null,
                pushPrivKey = null,
                unifiedPushUrl = null,
            )
        log.d { "subscription deleted" }
        accountRepository.update(updateAccount)
    }

    private suspend fun getSelectedDistributor(): String? = withContext(dispatcher) {
        UnifiedPush.getSavedDistributor(context)
    }

    private suspend fun registerForPushNotification(account: AccountModel) = withContext(dispatcher) {
        log.d { "registerForPushNotification" }
        // remove "=" padding some Mastodon instances use
        val vapid = nodeInfoRepository.getInfo()?.vapidKey?.replace("=", "")
        UnifiedPush.register(
            context = context,
            instance = account.channelId,
            vapid = vapid,
        )
    }

    private fun createNotificationChannelsIfNeeded(account: AccountModel) {
        NotificationType.ALL.forEach { type ->
            val channelId = type.getChannelId(account)
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel == null) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val newChannel = NotificationChannel(channelId, type.toString(), importance)
                notificationManager.createNotificationChannel(newChannel)
            }
        }
    }

    private suspend fun updateSubscription(account: AccountModel) {
        log.d { "updateSubscription" }
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

    private suspend fun unregisterForPushNotifications(account: AccountModel) = withContext(dispatcher) {
        log.d { "unregisterForPushNotifications" }
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
            NotificationType.Quote -> "quote"
            NotificationType.QuotedUpdate -> "quoted_update"
        }

private fun NotificationType.getChannelId(account: AccountModel): String? = buildString {
    val typeSegment = channelIdSegment ?: return null
    append(typeSegment)
    append(account.channelId)
}
