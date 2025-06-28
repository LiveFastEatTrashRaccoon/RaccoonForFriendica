package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import dev.mokkery.MockMode
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultHashtagProcessorTest {
    private val mainRouter = mock<MainRouter>(mode = MockMode.autoUnit)

    private val sut = DefaultHashtagProcessor(
        mainRouter = mainRouter,
        dispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `given valid URL in format 1 when process URL then interactions are as expected`() = runTest {
        val url = "https://$HOST/search?tag=$TAG"

        val res = sut.process(url)

        assertTrue(res)
        verify {
            mainRouter.openHashtag(TAG)
        }
    }

    @Test
    fun `given valid URL in format 2 when process URL then interactions are as expected`() = runTest {
        val url = "https://$HOST/tags/$TAG"

        val res = sut.process(url)

        assertTrue(res)
        verify {
            mainRouter.openHashtag(TAG)
        }
    }

    @Test
    fun `given invalid URL when process URL then interactions are as expected`() = runTest {
        val url = "https://$HOST/search?q=$TAG"

        val res = sut.process(url)

        assertFalse(res)
        verify(mode = VerifyMode.not) {
            mainRouter.openHashtag(any())
        }
    }

    companion object {
        private const val HOST = "example.com"
        private const val TAG = "test"
    }
}
