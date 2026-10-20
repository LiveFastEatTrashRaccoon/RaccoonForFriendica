package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetInnerUrlUseCaseTest {
    private val apiConfigurationRepository = mock<ApiConfigurationRepository>()
    private val sut =
        DefaultGetInnerUrlUseCase(
            apiConfigurationRepository = apiConfigurationRepository,
        )

    @Test
    fun `given base url when invoked then result is as expected`() = runTest {
        every {
            apiConfigurationRepository.node
        } returns mock { every { value } returns FAKE_INSTANCE }
        val post = PostModel(id = "0", url = FAKE_POST_URL, content = "")

        val res = sut(post)

        assertEquals(
            buildString {
                append("https://$FAKE_INSTANCE/search?q=")
                append(FAKE_POST_URL)
                append("&type=statuses")
                append("&resolve=true")
                append("&limit=1")
            },
            res,
        )
    }

    @Test
    fun `given base url not present when invoked then result is as expected`() = runTest {
        every {
            apiConfigurationRepository.node
        } returns mock { every { value } returns "" }
        val post = PostModel(id = "0", url = FAKE_POST_URL, content = "")

        val res = sut(post)

        assertNull(res)
    }

    companion object {
        private const val FAKE_INSTANCE = "example.com"
        private const val FAKE_POST_URL = "https://other.com/@someone/124072605638855217"
    }
}
