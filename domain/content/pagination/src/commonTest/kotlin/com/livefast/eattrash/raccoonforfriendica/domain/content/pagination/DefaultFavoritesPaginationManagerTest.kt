package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultFavoritesPaginationManagerTest {
    private val timelineEntryRepository = mock<TimelineEntryRepository>()
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<UserModel>().withEmojisIfMissing() } returnsArgAt 0
            everySuspend { any<TimelineEntryModel>().withEmojisIfMissing() } returnsArgAt 0
        }
    private val replyHelper =
        mock<ReplyHelper> {
            everySuspend { any<TimelineEntryModel>().withInReplyToIfMissing() } returnsArgAt 0
        }
    private val notificationCenter =
        mock<NotificationCenter> {
            every { subscribe(any<KClass<NotificationCenterEvent>>()) } returns MutableSharedFlow()
        }

    private val sut =
        DefaultFavoritesPaginationManager(
            timelineEntryRepository = timelineEntryRepository,
            emojiHelper = emojiHelper,
            replyHelper = replyHelper,
            notificationCenter = notificationCenter,
            dispatcher = UnconfinedTestDispatcher(),
        )

    // region Bookmarks
    @Test
    fun `given no results when loadNextPage with Bookmarks specification then result is as expected`() =
        runTest {
            everySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = any())
            } returns emptyList()

            sut.reset(FavoritesPaginationSpecification.Bookmarks(includeNsfw = false))
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = null)
            }
        }

    @Test
    fun `given results when loadNextPage with Bookmarks specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = any())
            } returns list

            sut.reset(FavoritesPaginationSpecification.Bookmarks(includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = null)
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with Bookmarks specification and not includeNsfw then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                    TimelineEntryModel(
                        id = "3",
                        content = "",
                        creator = UserModel(id = "2"),
                        sensitive = true,
                    ),
                )
            everySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = any())
            } returns list

            sut.reset(FavoritesPaginationSpecification.Bookmarks(includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1), res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = null)
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with Bookmarks specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                    TimelineEntryModel(
                        id = "3",
                        content = "",
                        creator = UserModel(id = "2"),
                        sensitive = true,
                    ),
                )
            everySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = any())
            } returns list

            sut.reset(FavoritesPaginationSpecification.Bookmarks(includeNsfw = true))
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = null)
            }
        }

    @Test
    fun `given no more results when loadNextPage twice with Bookmarks specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = any())
            } sequentiallyReturns listOf(list, emptyList())

            sut.reset(FavoritesPaginationSpecification.Bookmarks(includeNsfw = false))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = null)
                timelineEntryRepository.getBookmarks(pageCursor = "1")
            }
        }
    // endregion

    // region Favorites
    @Test
    fun `given no results when loadNextPage with Favorites specification then result is as expected`() =
        runTest {
            everySuspend {
                timelineEntryRepository.getFavorites(pageCursor = any())
            } returns emptyList()

            sut.reset(FavoritesPaginationSpecification.Favorites(includeNsfw = false))
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getFavorites(pageCursor = null)
            }
        }

    @Test
    fun `given results when loadNextPage with Favorites specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                timelineEntryRepository.getFavorites(pageCursor = any())
            } returns list

            sut.reset(FavoritesPaginationSpecification.Favorites(includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getFavorites(pageCursor = null)
            }
        }

    @Test
    fun `given no more results when loadNextPage twice with Favorites specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                timelineEntryRepository.getFavorites(pageCursor = any())
            } sequentiallyReturns listOf(list, emptyList())

            sut.reset(FavoritesPaginationSpecification.Favorites(includeNsfw = false))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getFavorites(pageCursor = null)
                timelineEntryRepository.getFavorites(pageCursor = "1")
            }
        }
    // endregion
}
