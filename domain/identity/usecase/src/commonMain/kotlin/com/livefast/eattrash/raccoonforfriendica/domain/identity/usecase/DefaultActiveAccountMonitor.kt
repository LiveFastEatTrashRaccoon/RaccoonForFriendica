package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.core.utils.nodeName
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toTimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class DefaultActiveAccountMonitor(
    private val accountRepository: AccountRepository,
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
    private val accountCredentialsCache: AccountCredentialsCache,
    private val settingsRepository: SettingsRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val inboxManager: InboxManager,
    private val contentPreloadManager: ContentPreloadManager,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ActiveAccountMonitor {
    private val scope = CoroutineScope(SupervisorJob() + coroutineDispatcher)

    override fun start() {
        scope.launch {
            accountRepository
                .getActiveAsFlow()
                .distinctUntilChanged()
                .onEach { account ->
                    process(account)
                }.launchIn(this)
            apiConfigurationRepository.node
                .onEach {
                    if (accountRepository.getActive()?.remoteId.isNullOrEmpty()) {
                        process(null)
                    }
                }.launchIn(this)
        }
    }

    override suspend fun isNotLoggedButItShould(): Boolean {
        val localAccountHasRemoteId = !accountRepository.getActive()?.remoteId.isNullOrEmpty()
        val remoteUser = identityRepository.currentUser.value
        return localAccountHasRemoteId && remoteUser == null
    }

    override suspend fun forceRefresh() {
        val account = accountRepository.getActive()
        process(account)
    }

    private suspend fun process(account: AccountModel?) {
        if (account == null) {
            apiConfigurationRepository.setAuth(null)

            identityRepository.refreshCurrentUser(null)

            supportedFeatureRepository.refresh()

            contentPreloadManager.preload()

            return
        }

        val node = account.handle.nodeName ?: apiConfigurationRepository.defaultNode
        apiConfigurationRepository.changeNode(node)

        val credentials = accountCredentialsCache.get(account.id)
        apiConfigurationRepository.setAuth(credentials)

        val defaultSettings = settingsRepository.get(account.id) ?: SettingsModel()

        contentPreloadManager.preload(
            userRemoteId = account.remoteId,
            defaultTimelineType = defaultSettings.defaultTimelineType.toTimelineType(),
        )

        identityRepository.refreshCurrentUser(account.remoteId)

        supportedFeatureRepository.refresh()

        settingsRepository.changeCurrent(defaultSettings)

        inboxManager.refreshUnreadCount()
    }
}
