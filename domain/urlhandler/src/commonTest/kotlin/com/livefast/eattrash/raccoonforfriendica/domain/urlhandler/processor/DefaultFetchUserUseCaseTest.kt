package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class DefaultFetchUserUseCaseTest {
    private val userRepository = mock<UserRepository>()
    private val sut =
        DefaultFetchUserUseCase(
            userRepository = userRepository,
        )

    @Test
    fun `given user found when invoke then result is as expected`() =
        runTest {
            val user = UserModel(id = "0", username = USERNAME)
            everySuspend { userRepository.getByHandle(any()) } returns user
            val handle = "$USERNAME@$HOST.com"

            val res = sut.invoke(handle)

            assertNotNull(res)
            verifySuspend {
                userRepository.getByHandle(handle)
            }
        }

    @Test
    fun `given user not found when invoke then result is as expected`() =
        runTest {
            everySuspend { userRepository.getByHandle(any()) } returns null
            val handle = "$USERNAME@$HOST.com"

            val res = sut.invoke(handle)

            assertNull(res)
            verifySuspend {
                userRepository.getByHandle(handle)
            }
        }

    @Test
    fun `given request timeout when invoke then result is as expected`() =
        runTest {
            everySuspend { userRepository.getByHandle(any()) } calls {
                delay(10.seconds)
                UserModel(id = "0", username = USERNAME)
            }
            val handle = "$USERNAME@$HOST.com"

            val res = sut.invoke(handle)

            assertNull(res)
            verifySuspend {
                userRepository.getByHandle(handle)
            }
        }

    companion object {
        private const val HOST = "example.com"
        private const val USERNAME = "username"
    }
}
