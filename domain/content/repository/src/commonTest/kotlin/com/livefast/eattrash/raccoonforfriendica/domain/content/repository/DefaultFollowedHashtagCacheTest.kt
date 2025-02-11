package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
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

class DefaultFollowedHashtagCacheTest {
    private val tagRepository = mock<TagRepository>()
    private val sut =
        DefaultFollowedHashtagCache(
            tagRepository = tagRepository,
        )

    @Test
    fun `given initial when getAll then result is as expected`() =
        runTest {
            val res = sut.getAll()

            assertEquals(emptyList(), res)
        }

    @Test
    fun `when refresh then result is as expected`() =
        runTest {
            val tags =
                listOf(
                    TagModel(name = "tag 1"),
                    TagModel(name = "tag 2"),
                )
            everySuspend {
                tagRepository.getFollowed(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list = tags),
                    ListWithPageCursor(list = emptyList()),
                )

            sut.refresh()
            val res = sut.getAll()

            assertEquals(tags, res)
            verifySuspend {
                tagRepository.getFollowed(pageCursor = null)
            }
        }

    @Test
    fun `given more pages when refresh then result is as expected`() =
        runTest {
            val page1 =
                listOf(
                    TagModel(name = "tag 1"),
                    TagModel(name = "tag 2"),
                )
            val page2 =
                listOf(
                    TagModel(name = "tag 1"),
                    TagModel(name = "tag 2"),
                )
            val cursor = "tagId"
            everySuspend {
                tagRepository.getFollowed(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list = page1, cursor = cursor),
                    ListWithPageCursor(list = page2),
                    ListWithPageCursor(list = emptyList()),
                )

            sut.refresh()
            val res = sut.getAll()

            assertEquals(page1 + page2, res)
            verifySuspend {
                tagRepository.getFollowed(pageCursor = null)
                tagRepository.getFollowed(pageCursor = cursor)
            }
        }

    @Test
    fun `when clear then result is as expected`() =
        runTest {
            val tags =
                listOf(
                    TagModel(name = "tag 1"),
                    TagModel(name = "tag 2"),
                )
            everySuspend {
                tagRepository.getFollowed(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list = tags),
                    ListWithPageCursor(list = emptyList()),
                )

            sut.refresh()
            sut.clear()
            val res = sut.getAll()

            assertEquals(emptyList(), res)
        }

    @Test
    fun `given tag is present when isFollowed then result is as expected`() =
        runTest {
            val tags =
                listOf(
                    TagModel(name = "tag 1"),
                    TagModel(name = "tag 2"),
                )
            everySuspend {
                tagRepository.getFollowed(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list = tags),
                    ListWithPageCursor(list = emptyList()),
                )

            sut.refresh()
            val res = sut.isFollowed(TagModel(name = "tag 2"))

            assertTrue(res)
        }

    @Test
    fun `given tag is not present when isFollowed then result is as expected`() =
        runTest {
            val tags =
                listOf(
                    TagModel(name = "tag 1"),
                    TagModel(name = "tag 2"),
                )
            everySuspend {
                tagRepository.getFollowed(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list = tags),
                    ListWithPageCursor(list = emptyList()),
                )

            sut.refresh()
            val res = sut.isFollowed(TagModel(name = "tag 3"))

            assertFalse(res)
        }
}
