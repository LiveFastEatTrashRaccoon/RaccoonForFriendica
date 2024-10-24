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

class DefaultPeertubeUserProcessorTest {
    private val detailOpener = mock<DetailOpener>(mode = MockMode.autoUnit)
    private val fetchUser = mock<FetchUserUseCase>()
    private val sut =
        DefaultPeertubeProcessor(
            detailOpener = detailOpener,
            fetchUser = fetchUser,
        )

    @Test
    fun `given valid user when process URL then interactions are as expected`() =
        runTest {
            val user = UserModel(id = "0", username = USERNAME)
            everySuspend { fetchUser.invoke(any()) } returns user
            val url = "https://$HOST/accounts/$USERNAME"

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
            val url = "https://$HOST/accounts/$USERNAME"

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
    fun `given valid channel when process URL then interactions are as expected`() =
        runTest {
            val user = UserModel(id = "0", username = USERNAME, group = true)
            everySuspend { fetchUser.invoke(any()) } returns user
            val url = "https://$HOST/video-channels/$USERNAME"

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
    fun `given channel not found when process URL then interactions are as expected`() =
        runTest {
            everySuspend { fetchUser.invoke(any()) } returns null
            val url = "https://$HOST/video-channels/$USERNAME"

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
            val url = "https://$HOST/profile/$USERNAME"

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
        private const val HOST = "example.com"
        private const val USERNAME = "username"
    }
}
