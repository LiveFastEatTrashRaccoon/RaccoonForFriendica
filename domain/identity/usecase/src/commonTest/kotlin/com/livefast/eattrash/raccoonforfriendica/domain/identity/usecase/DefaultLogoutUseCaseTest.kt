package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultLogoutUseCaseTest {
    private val apiConfigurationRepository =
        mock<ApiConfigurationRepository>(mode = MockMode.autoUnit)
    private val accountRepository = mock<AccountRepository>(mode = MockMode.autoUnit)
    private val sut =
        DefaultLogoutUseCase(
            apiConfigurationRepository,
            accountRepository,
        )

    @Test
    fun `when invoke then interactions are as expected`() = runTest {
        val accountData = AccountModel(id = 2)
        everySuspend { accountRepository.getActive() } returns accountData
        val anonymousAccount = AccountModel(id = 1)
        everySuspend { accountRepository.getBy(handle = any()) } returns anonymousAccount

        sut.invoke()

        verifySuspend {
            apiConfigurationRepository.setAuth(null)
            accountRepository.getActive()
            accountRepository.getBy("")
            accountRepository.setActive(account = anonymousAccount, active = true)
        }
    }

    @Test
    fun `given no anonymous account when invoke then interactions are as expected`() = runTest {
        val accountData = AccountModel(id = 2)
        everySuspend { accountRepository.getActive() } returns accountData
        everySuspend { accountRepository.getBy(handle = any()) } returns null

        sut.invoke()

        verifySuspend {
            apiConfigurationRepository.setAuth(null)
            accountRepository.getActive()
            accountRepository.getBy("")
            accountRepository.setActive(account = accountData, active = false)
        }
    }

    @Test
    fun `given no active account when invoke then interactions are as expected`() = runTest {
        everySuspend { accountRepository.getActive() } returns null
        val anonymousAccount = AccountModel(id = 1)
        everySuspend { accountRepository.getBy(handle = any()) } returns anonymousAccount

        sut.invoke()

        verifySuspend {
            apiConfigurationRepository.setAuth(null)
            accountRepository.getActive()
        }
        verifySuspend(mode = VerifyMode.not) {
            accountRepository.setActive(account = any(), active = any())
        }
    }
}
