package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultHashtagProcessorTest {
    private val detailOpener = mock<DetailOpener>(mode = MockMode.autoUnit)
    private val apiConfigurationRepository =
        mock<ApiConfigurationRepository> {
            every { node } returns
                MutableStateFlow(HOST)
        }
    private val sut =
        DefaultHashtagProcessor(
            detailOpener = detailOpener,
            apiConfigurationRepository = apiConfigurationRepository,
        )

    @Test
    fun `given valid user when process URL then interactions are as expected`() =
        runTest {
            val url = "https://$HOST/search?tag=$TAG"

            val res = sut.process(url)

            assertTrue(res)
            verify {
                detailOpener.openHashtag(TAG)
            }
        }

    @Test
    fun `given invalid URL when process URL then interactions are as expected`() =
        runTest {
            val url = "https://$HOST/search?q=$TAG"

            val res = sut.process(url)

            assertFalse(res)
            verify(mode = VerifyMode.not) {
                detailOpener.openHashtag(any())
            }
        }

    companion object {
        private const val HOST = "a.gup.pe"
        private const val TAG = "test"
    }
}
