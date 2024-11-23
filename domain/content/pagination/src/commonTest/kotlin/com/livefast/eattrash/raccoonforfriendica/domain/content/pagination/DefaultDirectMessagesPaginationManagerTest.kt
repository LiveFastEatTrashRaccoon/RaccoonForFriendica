package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.DirectMessageModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
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

class DefaultDirectMessagesPaginationManagerTest {
    private val directMessageRepository = mock<DirectMessageRepository>()
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<UserModel>().withEmojisIfMissing() } returnsArgAt 0
        }
    private val sut =
        DefaultDirectMessagesPaginationManager(
            directMessageRepository = directMessageRepository,
            emojiHelper = emojiHelper,
        )

    // region All
    @Test
    fun `given no messages when loadNextPage with All specification then result is as expected`() =
        runTest {
            everySuspend {
                directMessageRepository.getAll(
                    page = any(),
                    limit = any(),
                )
            } returns emptyList()

            sut.reset(DirectMessagesPaginationSpecification.All)
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                directMessageRepository.getAll(
                    page = 1,
                    limit = 40,
                )
            }
        }

    @Test
    fun `given messages when loadNextPage with All specification then result is as expected`() =
        runTest {
            val list = listOf(DirectMessageModel(id = "1", sender = UserModel(id = "2")))
            everySuspend {
                directMessageRepository.getAll(
                    page = any(),
                    limit = any(),
                )
            } returns list

            sut.reset(DirectMessagesPaginationSpecification.All)
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                directMessageRepository.getAll(
                    page = 1,
                    limit = 40,
                )
            }
        }

    @Test
    fun `given can not fetch more when loadNextPage twice with All specification then result is as expected`() =
        runTest {
            val list = listOf(DirectMessageModel(id = "1", sender = UserModel(id = "2")))
            everySuspend {
                directMessageRepository.getAll(
                    page = any(),
                    limit = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(DirectMessagesPaginationSpecification.All)
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                directMessageRepository.getAll(
                    page = 1,
                    limit = 40,
                )
                directMessageRepository.getAll(
                    page = 2,
                    limit = 40,
                )
            }
        }
    // endregion

    // region Replies
    @Test
    fun `given no messages when loadNextPage with Replies specification then result is as expected`() =
        runTest {
            everySuspend {
                directMessageRepository.getReplies(
                    parentUri = any(),
                    page = any(),
                )
            } returns emptyList()

            sut.reset(DirectMessagesPaginationSpecification.Replies(PARENT_URI))
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                directMessageRepository.getReplies(
                    parentUri = PARENT_URI,
                    page = 1,
                )
            }
        }

    @Test
    fun `given messages when loadNextPage with Replies specification then result is as expected`() =
        runTest {
            val list = listOf(DirectMessageModel(id = "1", sender = UserModel(id = "2")))
            everySuspend {
                directMessageRepository.getReplies(
                    parentUri = any(),
                    page = any(),
                )
            } returns list

            sut.reset(DirectMessagesPaginationSpecification.Replies(PARENT_URI))
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                directMessageRepository.getReplies(
                    parentUri = PARENT_URI,
                    page = 1,
                )
            }
        }

    @Test
    fun `given can not fetch more when loadNextPage twice with Replies specification then result is as expected`() =
        runTest {
            val list = listOf(DirectMessageModel(id = "1", sender = UserModel(id = "2")))
            everySuspend {
                directMessageRepository.getReplies(
                    parentUri = any(),
                    page = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(DirectMessagesPaginationSpecification.Replies(PARENT_URI))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                directMessageRepository.getReplies(
                    parentUri = PARENT_URI,
                    page = 1,
                )
                directMessageRepository.getReplies(
                    parentUri = PARENT_URI,
                    page = 2,
                )
            }
        }
    // endregion

    companion object {
        private const val PARENT_URI = "fake-message-uri"
    }
}
