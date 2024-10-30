package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MarkerRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultActiveAccountMonitorTest {
    private val accountRepository = mock<AccountRepository>(mode = MockMode.autoUnit)
    private val apiConfigurationRepository =
        mock<ApiConfigurationRepository>(mode = MockMode.autoUnit) {
            every { defaultNode } returns "default-instance"
            every { node } returns MutableStateFlow("test-instance")
        }
    private val identityRepository = mock<IdentityRepository>(mode = MockMode.autoUnit)
    private val accountCredentialsCache = mock<AccountCredentialsCache>(mode = MockMode.autoUnit)
    private val settingsRepository = mock<SettingsRepository>(mode = MockMode.autoUnit)
    private val supportedFeatureRepository =
        mock<SupportedFeatureRepository>(mode = MockMode.autoUnit) {
            every { features } returns MutableStateFlow(NodeFeatures())
        }
    private val inboxManager = mock<InboxManager>(mode = MockMode.autoUnit)
    private val contentPreloadManager = mock<ContentPreloadManager>(mode = MockMode.autoUnit)
    private val markerRepository =
        mock<MarkerRepository>(mode = MockMode.autoUnit) {
            everySuspend { get(any(), any()) } returns null
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sut =
        DefaultActiveAccountMonitor(
            accountRepository = accountRepository,
            apiConfigurationRepository = apiConfigurationRepository,
            identityRepository = identityRepository,
            accountCredentialsCache = accountCredentialsCache,
            settingsRepository = settingsRepository,
            supportedFeatureRepository = supportedFeatureRepository,
            inboxManager = inboxManager,
            contentPreloadManager = contentPreloadManager,
            markerRepository = markerRepository,
            coroutineDispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given no active account when started then interactions are as expected`() =
        runTest {
            val account: AccountModel? = null
            val accountChannel = Channel<AccountModel?>()
            every { accountRepository.getActiveAsFlow() } returns accountChannel.receiveAsFlow()
            everySuspend { accountRepository.getActive() } returns account
            val settings = SettingsModel()

            sut.start()
            accountChannel.send(account)

            verify {
                apiConfigurationRepository.setAuth(null)
            }
            verifySuspend {
                supportedFeatureRepository.refresh()
                identityRepository.refreshCurrentUser(null)
                supportedFeatureRepository.refresh()
                contentPreloadManager.preload()
                settingsRepository.changeCurrent(settings)
                inboxManager.clearUnreadCount()
            }
        }

    @Test
    fun `given active account when started then interactions are as expected`() =
        runTest {
            val accountId = 1L
            val userId = "fake-user-id"
            val account =
                AccountModel(id = accountId, handle = "user@example.com", remoteId = userId)
            val accountChannel = Channel<AccountModel?>()
            every { accountRepository.getActiveAsFlow() } returns accountChannel.receiveAsFlow()
            everySuspend { accountRepository.getActive() } returns account
            every { apiConfigurationRepository.node } returns MutableStateFlow("test-instance")
            val credentials = ApiCredentials.OAuth2("fake-access-token", "")
            every { accountCredentialsCache.get(any()) } returns credentials
            val settings = SettingsModel()
            everySuspend { settingsRepository.get(any()) } returns settings

            sut.start()
            accountChannel.send(account)

            verify {
                apiConfigurationRepository.setAuth(credentials)
            }
            verifySuspend {
                supportedFeatureRepository.refresh()
                identityRepository.refreshCurrentUser(userId)
                contentPreloadManager.preload(userId)
                settingsRepository.changeCurrent(settings)
                inboxManager.refreshUnreadCount()
            }
        }
}
