package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultIdentityRepositoryTest {
    private val userService = mock<UserService>(mode = MockMode.autoUnit)
    private val provider =
        mock<ServiceProvider> {
            every { users } returns userService
        }
    private val sut = DefaultIdentityRepository(provider = provider)

    @Test
    fun `given valid user id when refresh then result is as expected`() =
        runTest {
            val userId = "user-id"
            everySuspend {
                userService.getById(any())
            } returns Account(id = userId, acct = "fake-user-name", username = "fake-user-name")
            sut.refreshCurrentUser(userId)

            val res = sut.currentUser.value

            assertNotNull(res)
            assertEquals(userId, res.id)
            verifySuspend {
                userService.getById(userId)
            }
        }

    @Test
    fun `given invalid user id when refresh then result is as expected`() =
        runTest {
            val userId = "user-id"
            everySuspend {
                userService.getById(any())
            } throws IOException("Not found")
            sut.refreshCurrentUser(userId)

            val res = sut.currentUser.value

            assertNull(res)
            verifySuspend {
                userService.getById(userId)
            }
        }
}
