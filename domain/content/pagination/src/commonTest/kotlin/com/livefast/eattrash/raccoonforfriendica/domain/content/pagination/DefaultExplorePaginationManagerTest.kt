package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.LinkModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
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
class DefaultExplorePaginationManagerTest {
    private val trendingRepository = mock<TrendingRepository>()
    private val userRepository =
        mock<UserRepository> {
            everySuspend { getRelationships(any()) } returns emptyList()
        }
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
        DefaultExplorePaginationManager(
            trendingRepository = trendingRepository,
            userRepository = userRepository,
            emojiHelper = emojiHelper,
            replyHelper = replyHelper,
            notificationCenter = notificationCenter,
            dispatcher = UnconfinedTestDispatcher(),
        )

    // region Posts
    @Test
    fun `given no results when loadNextPage with Posts specification then result is as expected`() =
        runTest {
            everySuspend {
                trendingRepository.getEntries(offset = any())
            } returns emptyList()

            sut.reset(ExplorePaginationSpecification.Posts(includeNsfw = false))
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getEntries(offset = 0)
            }
        }

    @Test
    fun `given results when loadNextPage with Posts specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                trendingRepository.getEntries(offset = any())
            } returns list

            sut.reset(ExplorePaginationSpecification.Posts(includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.Entry(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getEntries(offset = 0)
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with Posts specification and not includeNsfw then result is as expected`() =
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
                trendingRepository.getEntries(offset = any())
            } returns list

            sut.reset(ExplorePaginationSpecification.Posts(includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1).map { ExploreItemModel.Entry(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getEntries(offset = 0)
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with Posts specification then result is as expected`() =
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
                trendingRepository.getEntries(offset = any())
            } returns list

            sut.reset(ExplorePaginationSpecification.Posts(includeNsfw = true))
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.Entry(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getEntries(offset = 0)
            }
        }

    @Test
    fun `given no more results when loadNextPage twice with Posts specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                trendingRepository.getEntries(offset = any())
            } sequentiallyReturns listOf(list, emptyList())

            sut.reset(ExplorePaginationSpecification.Posts(includeNsfw = false))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.Entry(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getEntries(offset = 0)
                trendingRepository.getEntries(offset = 1)
            }
        }
    // endregion

    // region Hashtags
    @Test
    fun `given no results when loadNextPage with Hashtags specification then result is as expected`() =
        runTest {
            everySuspend {
                trendingRepository.getHashtags(offset = any(), refresh = any())
            } returns emptyList()

            sut.reset(ExplorePaginationSpecification.Hashtags(refresh = false))
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getHashtags(offset = 0, refresh = false)
            }
        }

    @Test
    fun `given results when loadNextPage with Hashtags specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TagModel(name = "", url = ""),
                )
            everySuspend {
                trendingRepository.getHashtags(offset = any(), refresh = any())
            } returns list

            sut.reset(ExplorePaginationSpecification.Hashtags(refresh = false))
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.HashTag(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getHashtags(offset = 0, refresh = false)
            }
        }

    @Test
    fun `given no more results when loadNextPage twice with Hashtags specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TagModel(name = "", url = ""),
                )
            everySuspend {
                trendingRepository.getHashtags(offset = any(), refresh = any())
            } sequentiallyReturns listOf(list, emptyList())

            sut.reset(ExplorePaginationSpecification.Hashtags(refresh = false))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.HashTag(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getHashtags(offset = 0, refresh = false)
                trendingRepository.getHashtags(offset = 1, refresh = false)
            }
        }
    // endregion

    // region Links
    @Test
    fun `given no results when loadNextPage with Links specification then result is as expected`() =
        runTest {
            everySuspend {
                trendingRepository.getLinks(offset = any())
            } returns emptyList()

            sut.reset(ExplorePaginationSpecification.Links)
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getLinks(offset = 0)
            }
        }

    @Test
    fun `given results when loadNextPage with Links specification then result is as expected`() =
        runTest {
            val list = listOf(LinkModel(url = "fake-url"))
            everySuspend {
                trendingRepository.getLinks(offset = any())
            } returns list

            sut.reset(ExplorePaginationSpecification.Links)
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.Link(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getLinks(offset = 0)
            }
        }

    @Test
    fun `given no more results when loadNextPage twice with Links specification then result is as expected`() =
        runTest {
            val list = listOf(LinkModel(url = "fake-url"))
            everySuspend {
                trendingRepository.getLinks(offset = any())
            } sequentiallyReturns listOf(list, emptyList())

            sut.reset(ExplorePaginationSpecification.Links)
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.Link(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                trendingRepository.getLinks(offset = 0)
                trendingRepository.getLinks(offset = 1)
            }
        }
    // endregion

    // region Suggestions
    @Test
    fun `given no results when loadNextPage with Suggestions specification then result is as expected`() =
        runTest {
            everySuspend {
                userRepository.getSuggestions()
            } returns emptyList()

            sut.reset(ExplorePaginationSpecification.Suggestions)
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                userRepository.getSuggestions()
            }
        }

    @Test
    fun `given results when loadNextPage with Suggestions specification then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "1"))
            everySuspend {
                userRepository.getSuggestions()
            } returns list

            sut.reset(ExplorePaginationSpecification.Suggestions)
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.User(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                userRepository.getSuggestions()
                userRepository.getRelationships(listOf("1"))
            }
        }
    // endregion
}
