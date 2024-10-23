package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
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

class DefaultSetupAccountUseCaseTest {
    private val accountRepository = mock<AccountRepository>(mode = MockMode.autoUnit)
    private val settingsRepository = mock<SettingsRepository>(mode = MockMode.autoUnit)
    private val sut =
        DefaultSetupAccountUseCase(
            accountRepository = accountRepository,
            settingsRepository = settingsRepository,
        )

    @Test
    fun `given no accounts when invoke then interactions are as expected`() =
        runTest {
            everySuspend { accountRepository.getAll() } returns emptyList()
            val anonymousAccount = AccountModel(handle = "", id = 1)
            everySuspend { accountRepository.getBy(handle = any()) } returns anonymousAccount
            everySuspend { settingsRepository.get(any()) } returns null

            sut.invoke()

            verifySuspend {
                accountRepository.getAll()
                accountRepository.create(anonymousAccount.copy(id = 0))
                accountRepository.getBy("")
                settingsRepository.get(anonymousAccount.id)
                settingsRepository.create(SettingsModel(accountId = anonymousAccount.id))
                accountRepository.setActive(anonymousAccount, true)
            }
        }

    @Test
    fun `given existing anonymous account when invoke then interactions are as expected`() =
        runTest {
            val anonymousAccount = AccountModel(handle = "", id = 1)
            everySuspend { accountRepository.getAll() } returns listOf(anonymousAccount)

            sut.invoke()

            verifySuspend {
                accountRepository.getAll()
            }
            verifySuspend(mode = VerifyMode.not) {
                accountRepository.create(anonymousAccount.copy(id = 0))
                accountRepository.getBy("")
                settingsRepository.get(anonymousAccount.id)
                settingsRepository.create(SettingsModel())
                accountRepository.setActive(anonymousAccount, true)
            }
        }
}
