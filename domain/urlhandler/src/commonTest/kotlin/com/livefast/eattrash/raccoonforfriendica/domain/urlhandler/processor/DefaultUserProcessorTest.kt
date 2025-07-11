package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultUserProcessorTest {
    private val mainRouter = mock<MainRouter>(mode = MockMode.autoUnit)
    private val fetchUser = mock<FetchUserUseCase>()
    private val sut =
        DefaultUserProcessor(
            mainRouter = mainRouter,
            fetchUser = fetchUser,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given valid user when process URL then interactions are as expected`() = runTest {
        val user = UserModel(id = "0", username = USERNAME)
        everySuspend { fetchUser.invoke(any()) } returns user
        val url = "https://$HOST/users/$USERNAME"

        val res = sut.process(url)

        assertTrue(res)
        verifySuspend {
            fetchUser.invoke(url)
        }
        verify {
            mainRouter.openUserDetail(user)
        }
    }

    @Test
    fun `given user not found when process URL then interactions are as expected`() = runTest {
        everySuspend { fetchUser.invoke(any()) } returns null
        val url = "https://$HOST/users/$USERNAME"

        val res = sut.process(url)

        assertFalse(res)
        verifySuspend {
            fetchUser.invoke(url)
        }
        verify(mode = VerifyMode.not) {
            mainRouter.openUserDetail(any())
        }
    }

    @Test
    fun `given invalid URL when process URL then interactions are as expected`() = runTest {
        everySuspend { fetchUser.invoke(any()) } returns null
        val url = "https://$HOST/u/$USERNAME"

        val res = sut.process(url)

        assertFalse(res)
        verifySuspend {
            fetchUser.invoke(any())
        }
        verify(mode = VerifyMode.not) {
            mainRouter.openUserDetail(any())
        }
    }

    companion object {
        private const val HOST = "example.com"
        private const val USERNAME = "username"
    }
}
