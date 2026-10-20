package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
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
class DefaultPostProcessorTest {
    private val mainRouter = mock<MainRouter>(mode = MockMode.autoUnit)
    private val fetchPost = mock<FetchPostUseCase>()
    private val sut =
        DefaultPostProcessor(
            mainRouter = mainRouter,
            fetchPost = fetchPost,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given valid user when process URL then interactions are as expected`() = runTest {
        val post = PostModel(id = "0", content = "")
        everySuspend { fetchPost.invoke(any()) } returns post
        val url = "https://$HOST/display/$POST_ID"

        val res = sut.process(url)

        assertTrue(res)
        verifySuspend {
            fetchPost.invoke(url)
        }
        verify {
            mainRouter.openPostDetail(post)
        }
    }

    @Test
    fun `given user not found when process URL then interactions are as expected`() = runTest {
        everySuspend { fetchPost.invoke(any()) } returns null
        val url = "https://$HOST/display/$POST_ID"

        val res = sut.process(url)

        assertFalse(res)
        verifySuspend {
            fetchPost.invoke(url)
        }
        verify(mode = VerifyMode.not) {
            mainRouter.openPostDetail(any())
        }
    }

    @Test
    fun `given invalid URL when process URL then interactions are as expected`() = runTest {
        everySuspend { fetchPost.invoke(any()) } returns null
        val url = "https://$HOST/p/$POST_ID"

        val res = sut.process(url)

        assertFalse(res)
        verifySuspend {
            fetchPost.invoke(any())
        }
        verify(mode = VerifyMode.not) {
            mainRouter.openPostDetail(any())
        }
    }

    companion object {
        private const val HOST = "example.com"
        private const val POST_ID = "objectId"
    }
}
