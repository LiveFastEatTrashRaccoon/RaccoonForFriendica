package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultGuppeProcessorTest {
    private val detailOpener = mock<DetailOpener>(mode = MockMode.autoUnit)
    private val fetchUser = mock<FetchUserUseCase>()
    private val sut =
        DefaultGuppeProcessor(
            detailOpener = detailOpener,
            fetchUser = fetchUser,
        )

    @Test
    fun `given valid user when process URL then interactions are as expected`() =
        runTest {
            val user = UserModel(id = "0", username = USERNAME)
            everySuspend { fetchUser.invoke(any()) } returns user
            val url = "https://$HOST/u/$USERNAME"

            val res = sut.process(url)

            assertTrue(res)
            verifySuspend {
                fetchUser.invoke("$USERNAME@$HOST")
            }
            verify {
                detailOpener.openUserDetail(user)
            }
        }

    @Test
    fun `given user not found when process URL then interactions are as expected`() =
        runTest {
            everySuspend { fetchUser.invoke(any()) } returns null
            val url = "https://$HOST/u/$USERNAME"

            val res = sut.process(url)

            assertFalse(res)
            verifySuspend {
                fetchUser.invoke("$USERNAME@$HOST")
            }
            verify(mode = VerifyMode.not) {
                detailOpener.openUserDetail(any())
            }
        }

    @Test
    fun `given invalid URL when process URL then interactions are as expected`() =
        runTest {
            everySuspend { fetchUser.invoke(any()) } returns null
            val url = "https://example.com/u/$USERNAME"

            val res = sut.process(url)

            assertFalse(res)
            verifySuspend(mode = VerifyMode.not) {
                fetchUser.invoke(any())
            }
            verify(mode = VerifyMode.not) {
                detailOpener.openUserDetail(any())
            }
        }

    companion object {
        private const val HOST = "a.gup.pe"
        private const val USERNAME = "username"
    }
}
