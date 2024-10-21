package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultSwitchAccountUseCaseTest {
    private val accountRepository = mock<AccountRepository>(mode = MockMode.autoUnit)
    private val sut = DefaultSwitchAccountUseCase(accountRepository = accountRepository)

    @Test
    fun `when invoke then interactions are as expected`() =
        runTest {
            val account = AccountModel(id = 1)

            sut.invoke(account)

            verifySuspend {
                accountRepository.setActive(account, true)
            }
        }
}
