package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
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

class DefaultFetchEntryUseCaseTest {
    private val searchRepository = mock<SearchRepository>()
    private val sut =
        DefaultFetchEntryUseCase(
            searchRepository = searchRepository,
        )

    @Test
    fun `given entry found when invoke then result is as expected`() =
        runTest {
            val entry = TimelineEntryModel(id = "0", content = "", url = URL)
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } returns
                listOf(ExploreItemModel.Entry(entry))

            val res = sut.invoke(URL)

            assertNotNull(res)
            verifySuspend {
                searchRepository.search(
                    query = URL,
                    type = SearchResultType.Entries,
                    pageCursor = null,
                    resolve = true,
                )
            }
        }

    @Test
    fun `given entry not found when invoke then result is as expected`() =
        runTest {
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } returns emptyList()

            val res = sut.invoke(URL)

            assertNull(res)
            verifySuspend {
                searchRepository.search(
                    query = URL,
                    type = SearchResultType.Entries,
                    pageCursor = null,
                    resolve = true,
                )
            }
        }

    @Test
    fun `given request timeout when invoke then result is as expected`() =
        runTest {
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } calls {
                delay(10.seconds)
                listOf(
                    ExploreItemModel.Entry(
                        TimelineEntryModel(id = "0", content = "", url = URL),
                    ),
                )
            }

            val res = sut.invoke(URL)

            assertNull(res)
            verifySuspend {
                searchRepository.search(
                    query = URL,
                    type = SearchResultType.Entries,
                    pageCursor = null,
                    resolve = true,
                )
            }
        }

    companion object {
        private const val URL = "https://example.com/display/objectId"
    }
}
