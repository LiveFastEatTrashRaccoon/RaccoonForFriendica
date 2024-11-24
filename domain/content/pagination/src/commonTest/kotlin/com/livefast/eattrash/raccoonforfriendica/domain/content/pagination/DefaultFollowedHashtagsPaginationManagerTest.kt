package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultFollowedHashtagsPaginationManagerTest {
    private val tagRepository = mock<TagRepository>()
    private val sut = DefaultFollowedHashtagsPaginationManager(tagRepository = tagRepository)

    @Test
    fun `given no results when loadNextPage then result is as expected`() =
        runTest {
            everySuspend { tagRepository.getFollowed(any()) } returns ListWithPageCursor()

            sut.reset()
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                tagRepository.getFollowed(pageCursor = null)
            }
        }

    @Test
    fun `given results when loadNextPage then result is as expected`() =
        runTest {
            val elements =
                listOf(
                    TagModel(url = "fake-url", name = "fake-name", following = true),
                )
            everySuspend { tagRepository.getFollowed(any()) } returns
                ListWithPageCursor(list = elements, cursor = "1")

            sut.reset()
            val res = sut.loadNextPage()

            assertEquals(elements, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                tagRepository.getFollowed(pageCursor = null)
            }
        }

    @Test
    fun `given can not fetch more when loadNextPage twice then result is as expected`() =
        runTest {
            val elements =
                listOf(
                    TagModel(url = "fake-url", name = "fake-name", following = true),
                )
            everySuspend {
                tagRepository.getFollowed(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list = elements, cursor = "1"),
                    ListWithPageCursor(),
                )

            sut.reset()
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(elements, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                tagRepository.getFollowed(pageCursor = null)
                tagRepository.getFollowed(pageCursor = "1")
            }
        }
}
