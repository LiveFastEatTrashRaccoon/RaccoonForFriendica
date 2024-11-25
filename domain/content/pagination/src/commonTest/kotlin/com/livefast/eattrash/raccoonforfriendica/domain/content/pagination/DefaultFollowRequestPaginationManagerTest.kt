package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
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

class DefaultFollowRequestPaginationManagerTest {
    private val userRepository = mock<UserRepository>()
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<UserModel>().withEmojisIfMissing() } returnsArgAt 0
        }
    private val sut =
        DefaultFollowRequestPaginationManager(
            userRepository = userRepository,
            emojiHelper = emojiHelper,
        )

    @Test
    fun `given no results when loadNextPage then result is as expected`() =
        runTest {
            everySuspend { userRepository.getFollowRequests(any()) } returns ListWithPageCursor()

            sut.reset()
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                userRepository.getFollowRequests(pageCursor = null)
            }
        }

    @Test
    fun `given results when loadNextPage then result is as expected`() =
        runTest {
            val elements = listOf(UserModel(id = "1"))
            everySuspend { userRepository.getFollowRequests(any()) } returns
                ListWithPageCursor(
                    list = elements,
                    cursor = "1",
                )

            sut.reset()
            val res = sut.loadNextPage()

            assertEquals(elements, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                userRepository.getFollowRequests(pageCursor = null)
            }
        }

    @Test
    fun `given can not fetch more when loadNextPage twice then result is as expected`() =
        runTest {
            val elements = listOf(UserModel(id = "1"))
            everySuspend {
                userRepository.getFollowRequests(any())
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(
                        list = elements,
                        cursor = "1",
                    ),
                    ListWithPageCursor(),
                )

            sut.reset()
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(elements, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                userRepository.getFollowRequests(pageCursor = null)
                userRepository.getFollowRequests(pageCursor = "1")
            }
        }
}
