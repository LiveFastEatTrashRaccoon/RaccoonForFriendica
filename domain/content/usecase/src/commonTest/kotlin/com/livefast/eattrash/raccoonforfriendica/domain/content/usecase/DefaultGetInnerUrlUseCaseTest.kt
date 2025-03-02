package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
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
    fun `given base url when invoked then result is as expected`() =
        runTest {
            every {
                apiConfigurationRepository.node
            } returns mock { every { value } returns FAKE_INSTANCE }
            val entry = TimelineEntryModel(id = "0", url = FAKE_ENTRY_URL, content = "")

            val res = sut(entry)

            assertEquals(
                buildString {
                    append("https://$FAKE_INSTANCE/search?q=")
                    append(FAKE_ENTRY_URL)
                    append("&type=statuses")
                    append("&resolve=true")
                    append("&limit=1")
                },
                res,
            )
        }

    @Test
    fun `given base url not present when invoked then result is as expected`() =
        runTest {
            every {
                apiConfigurationRepository.node
            } returns mock { every { value } returns "" }
            val entry = TimelineEntryModel(id = "0", url = FAKE_ENTRY_URL, content = "")

            val res = sut(entry)

            assertNull(res)
        }

    companion object {
        private const val FAKE_INSTANCE = "example.com"
        private const val FAKE_ENTRY_URL = "https://other.com/@someone/124072605638855217"
    }
}
