package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
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
class DefaultUserPaginationManagerTest {
    private val userRepository =
        mock<UserRepository> {
            everySuspend { getRelationships(any()) } returns emptyList()
        }
    private val timelineEntryRepository = mock<TimelineEntryRepository>()
    private val circlesRepository = mock<CirclesRepository>()
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<UserModel>().withEmojisIfMissing() } returnsArgAt 0
            everySuspend { any<TimelineEntryModel>().withEmojisIfMissing() } returnsArgAt 0
        }
    private val notificationCenter =
        mock<NotificationCenter> {
            every { subscribe(any<KClass<NotificationCenterEvent>>()) } returns MutableSharedFlow()
        }
    private val accountRepository =
        mock<AccountRepository> {
            everySuspend { getActive() } returns null
        }
    private val userRateLimitRepository = mock<UserRateLimitRepository>()
    private val sut =
        DefaultUserPaginationManager(
            userRepository = userRepository,
            timelineEntryRepository = timelineEntryRepository,
            circlesRepository = circlesRepository,
            accountRepository = accountRepository,
            userRateLimitRepository = userRateLimitRepository,
            emojiHelper = emojiHelper,
            notificationCenter = notificationCenter,
            dispatcher = UnconfinedTestDispatcher(),
        )

    // region Follower
    @Test
    fun `given no results when loadNextPage with Follower then result is as expected`() = runTest {
        everySuspend {
            userRepository.getFollowers(
                id = any(),
                pageCursor = any(),
            )
        } returns emptyList()

        sut.reset(UserPaginationSpecification.Follower(userId = "1"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.getFollowers(id = "1", pageCursor = null)
        }
    }

    @Test
    fun `given results when loadNextPage with Follower then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            userRepository.getFollowers(
                id = any(),
                pageCursor = any(),
            )
        } returns list

        sut.reset(UserPaginationSpecification.Follower(userId = "1"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            userRepository.getFollowers(id = "1", pageCursor = null)
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Follower twice then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "2"))
            everySuspend {
                userRepository.getFollowers(
                    id = any(),
                    pageCursor = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(UserPaginationSpecification.Follower(userId = "1"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                userRepository.getFollowers(id = "1", pageCursor = null)
                userRepository.getFollowers(id = "1", pageCursor = "2")
            }
        }
    // endregion

    // region Following
    @Test
    fun `given no results when loadNextPage with Following then result is as expected`() = runTest {
        everySuspend {
            userRepository.getFollowing(
                id = any(),
                pageCursor = any(),
            )
        } returns emptyList()

        sut.reset(UserPaginationSpecification.Following(userId = "1"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.getFollowing(id = "1", pageCursor = null)
        }
    }

    @Test
    fun `given results when loadNextPage with Following then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            userRepository.getFollowing(
                id = any(),
                pageCursor = any(),
            )
        } returns list

        sut.reset(UserPaginationSpecification.Following(userId = "1"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            userRepository.getFollowing(id = "1", pageCursor = null)
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Following twice then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "2"))
            everySuspend {
                userRepository.getFollowing(
                    id = any(),
                    pageCursor = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(UserPaginationSpecification.Following(userId = "1"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                userRepository.getFollowing(id = "1", pageCursor = null)
                userRepository.getFollowing(id = "1", pageCursor = "2")
            }
        }
    // endregion

    // region EntryUsersReblog
    @Test
    fun `given no results when loadNextPage with EntryUsersReblog then result is as expected`() = runTest {
        everySuspend {
            timelineEntryRepository.getUsersWhoReblogged(
                id = any(),
                pageCursor = any(),
            )
        } returns emptyList()

        sut.reset(UserPaginationSpecification.EntryUsersReblog(entryId = "1"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            timelineEntryRepository.getUsersWhoReblogged(
                id = "1",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with EntryUsersReblog then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            timelineEntryRepository.getUsersWhoReblogged(
                id = any(),
                pageCursor = any(),
            )
        } returns list

        sut.reset(UserPaginationSpecification.EntryUsersReblog(entryId = "1"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            timelineEntryRepository.getUsersWhoReblogged(
                id = "1",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with EntryUsersReblog twice then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "2"))
            everySuspend {
                timelineEntryRepository.getUsersWhoReblogged(
                    id = any(),
                    pageCursor = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(UserPaginationSpecification.EntryUsersReblog(entryId = "1"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getUsersWhoReblogged(
                    id = "1",
                    pageCursor = null,
                )
                timelineEntryRepository.getUsersWhoReblogged(
                    id = "1",
                    pageCursor = "2",
                )
            }
        }
    // endregion

    // region EntryUsersFavorite
    @Test
    fun `given no results when loadNextPage with EntryUsersFavorite then result is as expected`() = runTest {
        everySuspend {
            timelineEntryRepository.getUsersWhoFavorited(
                id = any(),
                pageCursor = any(),
            )
        } returns emptyList()

        sut.reset(UserPaginationSpecification.EntryUsersFavorite(entryId = "1"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            timelineEntryRepository.getUsersWhoFavorited(
                id = "1",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with EntryUsersFavorite then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            timelineEntryRepository.getUsersWhoFavorited(
                id = any(),
                pageCursor = any(),
            )
        } returns list

        sut.reset(UserPaginationSpecification.EntryUsersFavorite(entryId = "1"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            timelineEntryRepository.getUsersWhoFavorited(
                id = "1",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given cannot fetch more results when loadNextPage with EntryUsersFavorite twice then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "2"))
            everySuspend {
                timelineEntryRepository.getUsersWhoFavorited(
                    id = any(),
                    pageCursor = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(UserPaginationSpecification.EntryUsersFavorite(entryId = "1"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getUsersWhoFavorited(
                    id = "1",
                    pageCursor = null,
                )
                timelineEntryRepository.getUsersWhoFavorited(
                    id = "1",
                    pageCursor = "2",
                )
            }
        }
    // endregion

    // region Search
    @Test
    fun `given no results when loadNextPage with Search then result is as expected`() = runTest {
        everySuspend {
            userRepository.search(
                query = any(),
                offset = any(),
                following = any(),
            )
        } returns emptyList()

        sut.reset(UserPaginationSpecification.Search(query = "query"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.search(
                query = "query",
                offset = 0,
                following = false,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with Search then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            userRepository.search(
                query = any(),
                offset = any(),
                following = any(),
            )
        } returns list

        sut.reset(UserPaginationSpecification.Search(query = "query"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            userRepository.search(
                query = "query",
                offset = 0,
                following = false,
            )
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Search twice then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            userRepository.search(
                query = any(),
                offset = any(),
                following = any(),
            )
        } sequentiallyReturns
            listOf(
                list,
                emptyList(),
            )

        sut.reset(UserPaginationSpecification.Search(query = "query"))
        sut.loadNextPage()
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.search(
                query = "query",
                offset = 0,
                following = false,
            )
            userRepository.search(
                query = "query",
                offset = 1,
                following = false,
            )
        }
    }
    // endregion

    // region SearchFollowing
    @Test
    fun `given no results when loadNextPage with SearchFollowing then result is as expected`() = runTest {
        everySuspend {
            userRepository.searchMyFollowing(
                query = any(),
                pageCursor = any(),
            )
        } returns emptyList()

        sut.reset(UserPaginationSpecification.SearchFollowing(query = "query"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.searchMyFollowing(
                query = "query",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with SearchFollowing then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend {
            userRepository.searchMyFollowing(
                query = any(),
                pageCursor = any(),
            )
        } returns list

        sut.reset(UserPaginationSpecification.SearchFollowing(query = "query"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            userRepository.searchMyFollowing(
                query = "query",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with SearchFollowing twice then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "2"))
            everySuspend {
                userRepository.searchMyFollowing(
                    query = any(),
                    pageCursor = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(UserPaginationSpecification.SearchFollowing(query = "query"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                userRepository.searchMyFollowing(
                    query = "query",
                    pageCursor = null,
                )
                userRepository.searchMyFollowing(
                    query = "query",
                    pageCursor = "2",
                )
            }
        }
    // endregion

    // region Muted
    @Test
    fun `given no results when loadNextPage with Muted then result is as expected`() = runTest {
        everySuspend { userRepository.getMuted(pageCursor = any()) } returns emptyList()

        sut.reset(UserPaginationSpecification.Muted)
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.getMuted(pageCursor = null)
        }
    }

    @Test
    fun `given results when loadNextPage with Muted then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend { userRepository.getMuted(pageCursor = any()) } returns list

        sut.reset(UserPaginationSpecification.Muted)
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            userRepository.getMuted(pageCursor = null)
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Muted twice then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend { userRepository.getMuted(pageCursor = any()) } sequentiallyReturns
            listOf(
                list,
                emptyList(),
            )

        sut.reset(UserPaginationSpecification.Muted)
        sut.loadNextPage()
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.getMuted(pageCursor = null)
            userRepository.getMuted(pageCursor = "2")
        }
    }
    // endregion

    // region Blocked
    @Test
    fun `given no results when loadNextPage with Blocked then result is as expected`() = runTest {
        everySuspend { userRepository.getBlocked(pageCursor = any()) } returns emptyList()

        sut.reset(UserPaginationSpecification.Blocked)
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.getBlocked(pageCursor = null)
        }
    }

    @Test
    fun `given results when loadNextPage with Blocked then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend { userRepository.getBlocked(pageCursor = any()) } returns list

        sut.reset(UserPaginationSpecification.Blocked)
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            userRepository.getBlocked(pageCursor = null)
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with Blocked twice then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2"))
        everySuspend { userRepository.getBlocked(pageCursor = any()) } sequentiallyReturns
            listOf(
                list,
                emptyList(),
            )

        sut.reset(UserPaginationSpecification.Blocked)
        sut.loadNextPage()
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRepository.getBlocked(pageCursor = null)
            userRepository.getBlocked(pageCursor = "2")
        }
    }
    // endregion

    // region CircleMembers
    @Test
    fun `given no results when loadNextPage with CircleMembers then result is as expected`() = runTest {
        everySuspend {
            circlesRepository.getMembers(id = any(), pageCursor = any())
        } returns emptyList()

        sut.reset(UserPaginationSpecification.CircleMembers(id = "1", query = "query"))
        val res = sut.loadNextPage()

        assertTrue(res.isEmpty())
        assertFalse(sut.canFetchMore)
        verifySuspend {
            circlesRepository.getMembers(
                id = "1",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given results when loadNextPage with CircleMembers then result is as expected`() = runTest {
        val list = listOf(UserModel(id = "2", displayName = "query"))
        everySuspend {
            circlesRepository.getMembers(id = any(), pageCursor = any())
        } returns list

        sut.reset(UserPaginationSpecification.CircleMembers(id = "1", query = "query"))
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertTrue(sut.canFetchMore)
        verifySuspend {
            circlesRepository.getMembers(
                id = "1",
                pageCursor = null,
            )
        }
    }

    @Test
    fun `given can not fetch more results when loadNextPage with CircleMembers twice then result is as expected`() =
        runTest {
            val list = listOf(UserModel(id = "2", displayName = "query"))
            everySuspend {
                circlesRepository.getMembers(id = any(), pageCursor = any())
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(UserPaginationSpecification.CircleMembers(id = "1", query = "query"))
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                circlesRepository.getMembers(
                    id = "1",
                    pageCursor = null,
                )
                circlesRepository.getMembers(
                    id = "1",
                    pageCursor = "2",
                )
            }
        }
    // endregion

    // region Limited
    @Test
    fun `given results when loadNextPage with Limited then result is as expected`() = runTest {
        val handle = "user"
        everySuspend {
            accountRepository.getActive()
        } returns AccountModel(id = 1, active = true)
        everySuspend {
            userRateLimitRepository.getAll(any())
        } returns
            listOf(
                UserRateLimitModel(id = 0, handle = handle, rate = 0.5),
            )
        val list =
            listOf(
                UserModel(
                    id = handle,
                    handle = handle,
                    displayName = handle,
                    username = "0.5",
                ),
            )

        sut.reset(UserPaginationSpecification.Limited)
        val res = sut.loadNextPage()

        assertEquals(list, res)
        assertFalse(sut.canFetchMore)
        verifySuspend {
            userRateLimitRepository.getAll(1)
        }
    }
    // endregion
}
