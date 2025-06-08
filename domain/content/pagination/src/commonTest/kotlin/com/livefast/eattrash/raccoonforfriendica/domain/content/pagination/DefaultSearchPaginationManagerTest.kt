package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
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
class DefaultSearchPaginationManagerTest {
    private val searchRepository = mock<SearchRepository>()
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
    private val accountRepository =
        mock<AccountRepository> {
            everySuspend { getActive() } returns null
        }
    private val stopWordRepository = mock<StopWordRepository>()

    private val sut =
        DefaultSearchPaginationManager(
            searchRepository = searchRepository,
            userRepository = userRepository,
            emojiHelper = emojiHelper,
            replyHelper = replyHelper,
            accountRepository = accountRepository,
            stopWordRepository = stopWordRepository,
            notificationCenter = notificationCenter,
            dispatcher = UnconfinedTestDispatcher(),
        )

    // region Posts
    @Test
    fun `given no results when loadNextPage with Entries specification then result is as expected`() = runTest {
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns emptyList()

        sut.reset(SearchPaginationSpecification.Entries(query = "query", includeNsfw = false))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Entries,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with Entries specification then result is as expected`() = runTest {
        val list =
            listOf(
                TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
            )
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns list.map { ExploreItemModel.Entry(it) }

        sut.reset(SearchPaginationSpecification.Entries(query = "query", includeNsfw = false))
        val res = sut.loadNextPage()

        assertEquals(list.map { ExploreItemModel.Entry(it) }, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Entries,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given sensitive results when loadNextPage with Entries spec and not includeNsfw then result is as expected`() =
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
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } returns list.map { ExploreItemModel.Entry(it) }

            sut.reset(SearchPaginationSpecification.Entries(query = "query", includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1).map { ExploreItemModel.Entry(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Entries,
                    pageCursor = null,
                    resolve = false,
                )
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with Entries specification then result is as expected`() = runTest {
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
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns list.map { ExploreItemModel.Entry(it) }

        sut.reset(SearchPaginationSpecification.Entries(query = "query", includeNsfw = true))
        val res = sut.loadNextPage()

        assertEquals(list.map { ExploreItemModel.Entry(it) }, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Entries,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given no more results when loadNextPage twice with Entries specification then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "", creator = UserModel(id = "2")),
                )
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } sequentiallyReturns listOf(list.map { ExploreItemModel.Entry(it) }, emptyList())

            sut.reset(SearchPaginationSpecification.Entries(query = "query", includeNsfw = false))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.Entry(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Entries,
                    pageCursor = null,
                    resolve = false,
                )
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Entries,
                    pageCursor = "1",
                    resolve = false,
                )
            }
        }

    @Test
    fun `given results and stopwords when loadNextPage with Entries specification then result is as expected`() =
        runTest {
            val accountId = 1L
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = "foo"),
                    TimelineEntryModel(
                        id = "3",
                        content = "bar",
                    ),
                )
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } returns list.map { ExploreItemModel.Entry(it) }
            everySuspend { accountRepository.getActive() } returns AccountModel(id = accountId)
            everySuspend { stopWordRepository.get(any()) } returns listOf("foo")

            sut.reset(SearchPaginationSpecification.Entries(query = "query", includeNsfw = false))
            val res = sut.loadNextPage()

            assertEquals(list.subList(1, 2).map { ExploreItemModel.Entry(it) }, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Entries,
                    pageCursor = null,
                    resolve = false,
                )
                accountRepository.getActive()
                stopWordRepository.get(accountId)
            }
        }
    // endregion

    // region Hashtags
    @Test
    fun `given no results when loadNextPage with Hashtags specification then result is as expected`() = runTest {
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns emptyList()

        sut.reset(SearchPaginationSpecification.Hashtags(query = "query"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Hashtags,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with Hashtags specification then result is as expected`() = runTest {
        val list = listOf(TagModel(name = "", url = ""))
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns list.map { ExploreItemModel.HashTag(it) }

        sut.reset(SearchPaginationSpecification.Hashtags(query = "query"))
        val res = sut.loadNextPage()

        assertEquals(list.map { ExploreItemModel.HashTag(it) }, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Hashtags,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given no more results when loadNextPage twice with Hashtags specification then result is as expected`() =
        runTest {
            val list = listOf(TagModel(name = "", url = ""))
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } sequentiallyReturns listOf(list.map { ExploreItemModel.HashTag(it) }, emptyList())

            sut.reset(SearchPaginationSpecification.Hashtags(query = "query"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.HashTag(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Hashtags,
                    pageCursor = null,
                    resolve = false,
                )
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Hashtags,
                    pageCursor = null,
                    resolve = false,
                )
            }
        }
    // endregion

    // region Users
    @Test
    fun `given no results when loadNextPage with Users specification then result is as expected`() = runTest {
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns emptyList()

        sut.reset(SearchPaginationSpecification.Users(query = "query"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with Users specification then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "1"))
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns list.map { ExploreItemModel.User(it) }

        sut.reset(SearchPaginationSpecification.Users(query = "query"))
        val res = sut.loadNextPage()

        assertEquals(list.map { ExploreItemModel.User(it) }, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given no more results when loadNextPage twice with Users specification then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "1"))
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } sequentiallyReturns listOf(list.map { ExploreItemModel.User(it) }, emptyList())

            sut.reset(SearchPaginationSpecification.Users(query = "query"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.User(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Users,
                    pageCursor = null,
                    resolve = false,
                )
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Users,
                    pageCursor = "1",
                    resolve = false,
                )
            }
        }
    // endregion

    // region Groups
    @Test
    fun `given no results when loadNextPage with Groups specification then result is as expected`() = runTest {
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns emptyList()

        sut.reset(SearchPaginationSpecification.Groups(query = "query"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with Groups specification then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "1", group = true), UserModel(id = "2", group = false))
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns list.map { ExploreItemModel.User(it) }

        sut.reset(SearchPaginationSpecification.Groups(query = "query"))
        val res = sut.loadNextPage()

        assertEquals(list.filter { it.group }.map { ExploreItemModel.User(it) }, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            searchRepository.search(
                query = "query",
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = false,
            )
        }
    }

    @Test
    fun `given no more results when loadNextPage twice with Groups specification then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "1", group = true))
            everySuspend {
                searchRepository.search(
                    query = any(),
                    type = any(),
                    pageCursor = any(),
                    resolve = any(),
                )
            } sequentiallyReturns listOf(list.map { ExploreItemModel.User(it) }, emptyList())

            sut.reset(SearchPaginationSpecification.Groups(query = "query"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.map { ExploreItemModel.User(it) }, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Users,
                    pageCursor = null,
                    resolve = false,
                )
                searchRepository.search(
                    query = "query",
                    type = SearchResultType.Users,
                    pageCursor = "1",
                    resolve = false,
                )
            }
        }
    // endregion
}
