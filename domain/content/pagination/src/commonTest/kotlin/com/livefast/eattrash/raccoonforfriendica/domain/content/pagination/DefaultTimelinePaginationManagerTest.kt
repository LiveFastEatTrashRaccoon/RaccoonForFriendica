package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.core.notifications.NotificationCenter
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.NotificationCenterEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
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
class DefaultTimelinePaginationManagerTest {
    private val timelineRepository = mock<TimelineRepository>()
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
    private val accountRepository =
        mock<AccountRepository> {
            everySuspend { getActive() } returns null
        }
    private val userRateLimitRepository = mock<UserRateLimitRepository>()
    private val stopWordRepository = mock<StopWordRepository>()
    private val sut =
        DefaultTimelinePaginationManager(
            timelineRepository = timelineRepository,
            timelineEntryRepository = timelineEntryRepository,
            accountRepository = accountRepository,
            userRateLimitRepository = userRateLimitRepository,
            emojiHelper = emojiHelper,
            replyHelper = replyHelper,
            stopWordRepository = stopWordRepository,
            notificationCenter = notificationCenter,
            dispatcher = UnconfinedTestDispatcher(),
        )

    // region Feed
    @Test
    fun `given no results when loadNextPage with public Feed then result is as expected`() =
        runTest {
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns emptyList()

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given results when loadNextPage with public Feed then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with public Feed with includeNsfw then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = ""),
                    TimelineEntryModel(id = "2", content = "", sensitive = true),
                )
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = false,
                    includeNsfw = true,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given sensitive results when loadNextPage with public Feed with no includeNsfw then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = ""),
                    TimelineEntryModel(id = "2", content = "", sensitive = true),
                )
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1), res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given results with replies when loadNextPage with public Feed with no includeNsfw then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(id = "1", content = ""),
                    TimelineEntryModel(
                        id = "2",
                        content = "",
                        inReplyTo = TimelineEntryModel(id = "3", content = ""),
                    ),
                )
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = true,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1), res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given results when loadNextPage with public Feed refresh then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = true,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = true,
                )
            }
        }

    @Test
    fun `given results when loadNextPage with home Feed then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getHome(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.Subscriptions,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getHome(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given results when loadNextPage with local Feed then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getLocal(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.Local,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getLocal(
                    pageCursor = null,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given results when loadNextPage with circle Feed then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getCircle(
                    id = any(),
                    pageCursor = any(),
                )
            } returns list

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.Circle(CircleModel(id = "1")),
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getCircle(
                    id = "1",
                    pageCursor = null,
                )
            }
        }

    @Test
    fun `given can fetch no more results when loadNextPage with public Feed then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getPublic(
                    pageCursor = any(),
                    refresh = any(),
                )
            } sequentiallyReturns listOf(list, emptyList())

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.All,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getPublic(
                    pageCursor = null,
                    refresh = false,
                )
                timelineRepository.getPublic(
                    pageCursor = "1",
                    refresh = false,
                )
            }
        }

    @Test
    fun `given results when loadNextPage with home Feed and rate limit then result is as expected`() =
        runTest {
            val accountId = 1L
            val list =
                listOf(
                    TimelineEntryModel(
                        id = "1",
                        content = "",
                        creator = UserModel(id = "1", handle = "user-1"),
                    ),
                    TimelineEntryModel(
                        id = "2",
                        content = "",
                        creator = UserModel(id = "2", handle = "user-2"),
                    ),
                    TimelineEntryModel(
                        id = "3",
                        content = "",
                        creator = UserModel(id = "1", handle = "user-1"),
                    ),
                )
            everySuspend {
                timelineRepository.getHome(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list
            everySuspend { accountRepository.getActive() } returns AccountModel(id = accountId)
            everySuspend { userRateLimitRepository.getAll(any()) } returns
                listOf(
                    UserRateLimitModel(handle = "user-1", rate = 0.5),
                )
            everySuspend { stopWordRepository.get(any()) } returns emptyList()

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.Subscriptions,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 2), res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getHome(
                    pageCursor = null,
                    refresh = false,
                )
                accountRepository.getActive()
                userRateLimitRepository.getAll(accountId)
                stopWordRepository.get(accountId)
            }
        }

    @Test
    fun `given results when loadNextPage with home Feed and stopwords then result is as expected`() =
        runTest {
            val accountId = 1L
            val list =
                listOf(
                    TimelineEntryModel(
                        id = "1",
                        content = "foo",
                    ),
                    TimelineEntryModel(
                        id = "2",
                        content = "bar",
                    ),
                    TimelineEntryModel(
                        id = "3",
                        content = "baz",
                    ),
                )
            everySuspend {
                timelineRepository.getHome(
                    pageCursor = any(),
                    refresh = any(),
                )
            } returns list
            everySuspend { accountRepository.getActive() } returns AccountModel(id = accountId)
            everySuspend { userRateLimitRepository.getAll(any()) } returns emptyList()
            everySuspend { stopWordRepository.get(any()) } returns listOf("foo")

            sut.reset(
                TimelinePaginationSpecification.Feed(
                    timelineType = TimelineType.Subscriptions,
                    excludeReplies = false,
                    includeNsfw = false,
                    refresh = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list.subList(1, 3), res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getHome(
                    pageCursor = null,
                    refresh = false,
                )
                accountRepository.getActive()
                userRateLimitRepository.getAll(accountId)
                stopWordRepository.get(accountId)
            }
        }
    // endregion

    // region Hashtag
    @Test
    fun `given no results when loadNextPage with Hashtag then result is as expected`() =
        runTest {
            everySuspend {
                timelineRepository.getHashtag(
                    hashtag = any(),
                    pageCursor = any(),
                )
            } returns ListWithPageCursor()

            sut.reset(
                TimelinePaginationSpecification.Hashtag(
                    hashtag = "tag",
                    includeNsfw = false,
                ),
            )
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getHashtag(
                    hashtag = "tag",
                    pageCursor = null,
                )
            }
        }

    @Test
    fun `given can fetch no more results when loadNextPage with Hashtag then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))
            everySuspend {
                timelineRepository.getHashtag(
                    hashtag = any(),
                    pageCursor = any(),
                )
            } sequentiallyReturns
                listOf(
                    ListWithPageCursor(list, "1"),
                    ListWithPageCursor(),
                )

            sut.reset(
                TimelinePaginationSpecification.Hashtag(
                    hashtag = "tag",
                    includeNsfw = false,
                ),
            )
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getHashtag(
                    hashtag = "tag",
                    pageCursor = null,
                )
                timelineRepository.getHashtag(
                    hashtag = "tag",
                    pageCursor = "1",
                )
            }
        }

    @Test
    fun `given results when loadNextPage with Hashtag and stopwords then result is as expected`() =
        runTest {
            val accountId = 1L
            val list =
                listOf(
                    TimelineEntryModel(
                        id = "1",
                        content = "foo",
                    ),
                    TimelineEntryModel(
                        id = "2",
                        content = "bar",
                    ),
                    TimelineEntryModel(
                        id = "3",
                        content = "baz",
                    ),
                )
            everySuspend {
                timelineRepository.getHashtag(
                    hashtag = any(),
                    pageCursor = any(),
                )
            } returns ListWithPageCursor(list = list, cursor = "1")
            everySuspend { accountRepository.getActive() } returns AccountModel(id = accountId)
            everySuspend { userRateLimitRepository.getAll(any()) } returns emptyList()
            everySuspend { stopWordRepository.get(any()) } returns listOf("foo")

            sut.reset(
                TimelinePaginationSpecification.Hashtag(
                    hashtag = "tag",
                    includeNsfw = false,
                ),
            )
            val res = sut.loadNextPage()

            assertEquals(list.subList(1, 3), res)
            assertTrue(sut.canFetchMore)
            verifySuspend {
                timelineRepository.getHashtag(
                    hashtag = "tag",
                    pageCursor = null,
                )
                accountRepository.getActive()
                userRateLimitRepository.getAll(accountId)
                stopWordRepository.get(accountId)
            }
        }
    // endregion

    // region User
    @Test
    fun `given no results when loadNextPage with User then result is as expected`() =
        runTest {
            everySuspend {
                timelineEntryRepository.getByUser(
                    userId = any(),
                    excludeReplies = any(),
                    pageCursor = any(),
                    excludeReblogs = any(),
                    pinned = any(),
                    onlyMedia = any(),
                    enableCache = any(),
                    refresh = any(),
                )
            } returns emptyList()

            sut.reset(
                TimelinePaginationSpecification.User(
                    userId = "1",
                    includeNsfw = false,
                ),
            )
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = null,
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given can fetch no more results when loadNextPage with User with includeNsfw then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(
                        id = "2",
                        content = "",
                    ),
                    TimelineEntryModel(
                        id = "3",
                        content = "",
                        sensitive = true,
                    ),
                )
            everySuspend {
                timelineEntryRepository.getByUser(
                    userId = any(),
                    excludeReplies = any(),
                    pageCursor = any(),
                    excludeReblogs = any(),
                    pinned = any(),
                    onlyMedia = any(),
                    enableCache = any(),
                    refresh = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(
                TimelinePaginationSpecification.User(
                    userId = "1",
                    includeNsfw = true,
                    onlyMedia = false,
                    excludeReplies = true,
                    excludeReblogs = false,
                    pinned = false,
                ),
            )
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list, res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = null,
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = "3",
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given can fetch no more results when loadNextPage with User with no includeNsfw then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(
                        id = "2",
                        content = "",
                    ),
                    TimelineEntryModel(
                        id = "3",
                        content = "",
                        sensitive = true,
                    ),
                )
            everySuspend {
                timelineEntryRepository.getByUser(
                    userId = any(),
                    excludeReplies = any(),
                    pageCursor = any(),
                    excludeReblogs = any(),
                    pinned = any(),
                    onlyMedia = any(),
                    enableCache = any(),
                    refresh = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(
                TimelinePaginationSpecification.User(
                    userId = "1",
                    includeNsfw = false,
                    onlyMedia = false,
                    excludeReplies = true,
                    excludeReblogs = false,
                    pinned = false,
                ),
            )
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1), res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = null,
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = "3",
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
            }
        }
    // endregion

    // region Forum
    @Test
    fun `given no results when loadNextPage with Forum then result is as expected`() =
        runTest {
            everySuspend {
                timelineEntryRepository.getByUser(
                    userId = any(),
                    excludeReplies = any(),
                    pageCursor = any(),
                    excludeReblogs = any(),
                    pinned = any(),
                    onlyMedia = any(),
                    enableCache = any(),
                    refresh = any(),
                )
            } returns emptyList()

            sut.reset(
                TimelinePaginationSpecification.Forum(
                    userId = "1",
                    includeNsfw = false,
                ),
            )
            val res = sut.loadNextPage()

            assertTrue(res.isEmpty())
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = null,
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
            }
        }

    @Test
    fun `given can fetch no more results when loadNextPage with Forum then result is as expected`() =
        runTest {
            val list =
                listOf(
                    TimelineEntryModel(
                        id = "2",
                        content = "",
                        reblog =
                            TimelineEntryModel(
                                id = "2-r",
                                content = "",
                            ),
                    ),
                    TimelineEntryModel(
                        id = "3",
                        content = "",
                        reblog =
                            TimelineEntryModel(
                                id = "3-r",
                                content = "",
                                inReplyTo = TimelineEntryModel(id = "4", content = ""),
                            ),
                    ),
                )
            everySuspend {
                timelineEntryRepository.getByUser(
                    userId = any(),
                    excludeReplies = any(),
                    pageCursor = any(),
                    excludeReblogs = any(),
                    pinned = any(),
                    onlyMedia = any(),
                    enableCache = any(),
                    refresh = any(),
                )
            } sequentiallyReturns
                listOf(
                    list,
                    emptyList(),
                )

            sut.reset(
                TimelinePaginationSpecification.Forum(
                    userId = "1",
                    includeNsfw = false,
                ),
            )
            sut.loadNextPage()
            val res = sut.loadNextPage()

            assertEquals(list.subList(0, 1), res)
            assertFalse(sut.canFetchMore)
            verifySuspend {
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = null,
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
                timelineEntryRepository.getByUser(
                    userId = "1",
                    excludeReplies = true,
                    pageCursor = "3",
                    excludeReblogs = false,
                    pinned = false,
                    onlyMedia = false,
                    refresh = false,
                )
            }
        }
    // endregion

    // region Bookmarks
    @Test
    fun `given no results when loadNextPage with Bookmarks specification then result is as expected`() =
        runTest {
            everySuspend {
                timelineEntryRepository.getBookmarks(pageCursor = any())
            } returns emptyList()

            sut.reset(TimelinePaginationSpecification.Bookmarks(includeNsfw = false))
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

            sut.reset(TimelinePaginationSpecification.Bookmarks(includeNsfw = false))
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

            sut.reset(TimelinePaginationSpecification.Bookmarks(includeNsfw = false))
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

            sut.reset(TimelinePaginationSpecification.Bookmarks(includeNsfw = true))
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

            sut.reset(TimelinePaginationSpecification.Bookmarks(includeNsfw = false))
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

            sut.reset(TimelinePaginationSpecification.Favorites(includeNsfw = false))
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

            sut.reset(TimelinePaginationSpecification.Favorites(includeNsfw = false))
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

            sut.reset(TimelinePaginationSpecification.Favorites(includeNsfw = false))
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

    // region history restoration
    @Test
    fun `given results when restore history with public Feed then result is as expected`() =
        runTest {
            val list = listOf(TimelineEntryModel(id = "1", content = ""))

            sut.restoreHistory(list)
            val res = sut.history

            assertEquals(list, res)
        }
    // endregion
}
