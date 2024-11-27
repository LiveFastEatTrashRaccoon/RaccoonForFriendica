package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DefaultLoginUseCaseTest {
    private val apiConfigurationRepository =
        mock<ApiConfigurationRepository>(mode = MockMode.autoUnit)
    private val credentialsRepository = mock<CredentialsRepository>(mode = MockMode.autoUnit)
    private val accountRepository = mock<AccountRepository>(mode = MockMode.autoUnit)
    private val settingsRepository = mock<SettingsRepository>(mode = MockMode.autoUnit)
    private val accountCredentialsCache = mock<AccountCredentialsCache>(mode = MockMode.autoUnit)
    private val supportedFeatureRepository =
        mock<SupportedFeatureRepository>(mode = MockMode.autoUnit) {
            every { features } returns MutableStateFlow(NodeFeatures())
        }
    private val sut =
        DefaultLoginUseCase(
            apiConfigurationRepository = apiConfigurationRepository,
            credentialsRepository = credentialsRepository,
            accountRepository = accountRepository,
            settingsRepository = settingsRepository,
            accountCredentialsCache = accountCredentialsCache,
            supportedFeatureRepository = supportedFeatureRepository,
        )

    @Test
    fun `given invalid credentials when execute then interactions are as expected`() =
        runTest {
            everySuspend { credentialsRepository.validate(any(), any()) } returns null
            val node = "example.com"
            val credentials = ApiCredentials.OAuth2("fake-access-token", "")

            assertFailsWith<IllegalStateException> {
                sut.invoke(node = node, credentials = credentials)
            }

            verifySuspend {
                apiConfigurationRepository.changeNode(node)
                apiConfigurationRepository.setAuth(credentials)
                credentialsRepository.validate(node, credentials)
            }
            verifySuspend(mode = VerifyMode.not) {
                accountRepository.getBy(handle = any())
                accountRepository.create(any())
                accountCredentialsCache.get(any())
                settingsRepository.get(any())
                settingsRepository.create(any())
                supportedFeatureRepository.features
            }
        }

    @Test
    fun `given valid credentials and not existing account when execute then interactions are as expected`() =
        runTest {
            val node = "example.com"
            val username = "fake-username"
            val userId = "0"
            everySuspend {
                credentialsRepository.validate(node = any(), credentials = any())
            } returns UserModel(id = userId, username = username)
            val accountData =
                AccountModel(
                    id = 1,
                    handle = "$username@$node",
                    remoteId = userId,
                )
            val credentials = ApiCredentials.OAuth2("fake-access-token", "")
            everySuspend { accountRepository.getBy(handle = any()) } sequentiallyReturns
                listOf(
                    null,
                    accountData,
                    AccountModel(),
                )
            everySuspend { settingsRepository.get(any()) } returns null

            sut.invoke(node = node, credentials = credentials)

            verifySuspend {
                apiConfigurationRepository.changeNode(node)
                apiConfigurationRepository.setAuth(credentials)
                credentialsRepository.validate(node, credentials)
                accountRepository.getBy("$username@$node")
                accountRepository.create(accountData.copy(id = 0))
                accountRepository.getBy("$username@$node")
                accountCredentialsCache.save(accountId = accountData.id, credentials)
                settingsRepository.get(accountData.id)
                settingsRepository.create(SettingsModel(accountId = accountData.id))
                accountRepository.setActive(accountData, true)
                supportedFeatureRepository.features
            }
        }

    @Test
    fun `given valid credentials on Friendica and not existing account when execute then interactions are as expected`() =
        runTest {
            val node = "example.com"
            val username = "fake-username"
            val userId = "0"
            everySuspend { supportedFeatureRepository.features } returns
                MutableStateFlow(NodeFeatures(supportsBBCode = true))
            everySuspend {
                credentialsRepository.validate(node = any(), credentials = any())
            } returns UserModel(id = userId, username = username)
            val accountData =
                AccountModel(
                    id = 1,
                    handle = "$username@$node",
                    remoteId = userId,
                )
            val credentials = ApiCredentials.OAuth2("fake-access-token", "")
            everySuspend { accountRepository.getBy(handle = any()) } sequentiallyReturns
                listOf(
                    null,
                    accountData,
                    AccountModel(),
                )
            everySuspend { settingsRepository.get(any()) } returns null

            sut.invoke(node = node, credentials = credentials)

            verifySuspend {
                apiConfigurationRepository.changeNode(node)
                apiConfigurationRepository.setAuth(credentials)
                credentialsRepository.validate(node, credentials)
                accountRepository.getBy("$username@$node")
                accountRepository.create(accountData.copy(id = 0))
                accountRepository.getBy("$username@$node")
                accountCredentialsCache.save(accountId = accountData.id, credentials)
                settingsRepository.get(accountData.id)
                settingsRepository.create(
                    SettingsModel(
                        accountId = accountData.id,
                        markupMode = MarkupMode.BBCode,
                        excludeRepliesFromTimeline = true,
                    ),
                )
                accountRepository.setActive(accountData, true)
                supportedFeatureRepository.features
            }
        }

    @Test
    fun `given valid credentials and existing account when execute then interactions are as expected`() =
        runTest {
            val node = "example.com"
            val username = "fake-username"
            val userId = "0"
            everySuspend {
                credentialsRepository.validate(node = any(), credentials = any())
            } returns UserModel(id = userId, username = username)
            val accountData =
                AccountModel(
                    id = 1,
                    handle = "$username@$node",
                    remoteId = userId,
                )
            val credentials = ApiCredentials.OAuth2("fake-access-token", "")
            everySuspend { accountRepository.getBy(handle = any()) } returns accountData
            everySuspend { settingsRepository.get(any()) } returns null

            sut.invoke(node = node, credentials = credentials)

            verifySuspend {
                apiConfigurationRepository.changeNode(node)
                apiConfigurationRepository.setAuth(credentials)
                credentialsRepository.validate(node, credentials)
                accountRepository.getBy("$username@$node")
                accountRepository.getBy("$username@$node")
                accountCredentialsCache.save(accountId = accountData.id, credentials)
                settingsRepository.get(accountData.id)
                settingsRepository.create(SettingsModel(accountId = accountData.id))
                accountRepository.setActive(accountData, true)
                supportedFeatureRepository.features
            }
            verifySuspend(mode = VerifyMode.not) {
                accountRepository.create(accountData)
            }
        }
}
