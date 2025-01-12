package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DefaultDeleteAccountUseCaseTest {
    private val accountRepository =
        mock<AccountRepository>(mode = MockMode.autoUnit) {
            everySuspend { getActive() } returns null
        }
    private val settingsRepository = mock<SettingsRepository>(mode = MockMode.autoUnit)
    private val accountCredentialsCache = mock<AccountCredentialsCache>(mode = MockMode.autoUnit)
    private val sut =
        DefaultDeleteAccountUseCase(
            accountRepository = accountRepository,
            settingsRepository = settingsRepository,
            accountCredentialsCache = accountCredentialsCache,
        )

    @Test
    fun `given account is not the active one when executed then interactions are as expected`() =
        runTest {
            val accountId = 1L
            val account = AccountModel(id = accountId)
            val settings = SettingsModel()
            everySuspend { settingsRepository.get(any()) } returns settings

            sut(account)

            verifySuspend {
                accountRepository.getActive()
                accountCredentialsCache.remove(accountId)
                settingsRepository.delete(settings)
                accountRepository.delete(account)
            }
        }

    @Test
    @Throws(IllegalStateException::class)
    fun `given account is active when executed then interactions are as expected`() =
        runTest {
            val accountId = 1L
            val account = AccountModel(id = accountId)
            everySuspend { accountRepository.getActive() } returns account

            assertFailsWith<IllegalStateException> {
                sut(account)
            }

            verifySuspend {
                accountRepository.getActive()
            }
            verifySuspend(mode = VerifyMode.not) {
                accountCredentialsCache.remove(accountId)
                settingsRepository.delete(any())
                accountRepository.delete(any())
            }
        }
}
