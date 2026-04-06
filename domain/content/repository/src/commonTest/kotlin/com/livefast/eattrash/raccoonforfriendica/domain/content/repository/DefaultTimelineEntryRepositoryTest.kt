package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Poll
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusContext
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.StatusSource
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PollService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.Visibility
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultTimelineEntryRepositoryTest {
    private val userService = mock<UserService>()
    private val statusService = mock<StatusService>()
    private val pollService = mock<PollService>()

    private val provider =
        mock<ServiceProvider> {
            every { user } returns userService
            every { status } returns statusService
            every { poll } returns pollService
        }

    private val otherProvider =
        mock<ServiceProvider>(MockMode.autoUnit) {
            every { user } returns userService
            every { status } returns statusService
            every { poll } returns pollService
        }

    private val sut =
        DefaultTimelineEntryRepository(
            provider = provider,
            otherProvider = otherProvider,
        )

    // region getByUser
    @Test
    fun `given error when getByUser then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.getStatuses(
                id = any(),
                maxId = any(),
                minId = any(),
                onlyMedia = any(),
                excludeReplies = any(),
                excludeReblogs = any(),
                pinned = any(),
                limit = any(),
            )
        } throws IOException("Network error")

        val res =
            sut.getByUser(
                userId = "userId",
                pageCursor = null,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                onlyMedia = false,
                enableCache = false,
                refresh = true,
            )

        assertNull(res)
        verifySuspend {
            userService.getStatuses(
                id = "userId",
                maxId = null,
                minId = null,
                onlyMedia = false,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                limit = 20,
            )
        }
    }

    @Test
    fun `given no results when getByUser then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.getStatuses(
                id = any(),
                maxId = any(),
                minId = any(),
                onlyMedia = any(),
                excludeReplies = any(),
                excludeReblogs = any(),
                pinned = any(),
                limit = any(),
            )
        } returns emptyList()

        val res =
            sut.getByUser(
                userId = "userId",
                pageCursor = null,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                onlyMedia = false,
                enableCache = false,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(emptyList(), res)
        verifySuspend {
            userService.getStatuses(
                id = "userId",
                maxId = null,
                minId = null,
                onlyMedia = false,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                limit = 20,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.user
        }
    }

    @Test
    fun `given results when getByUser then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.getStatuses(
                id = any(),
                maxId = any(),
                minId = any(),
                onlyMedia = any(),
                excludeReplies = any(),
                excludeReblogs = any(),
                pinned = any(),
                limit = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getByUser(
                userId = "userId",
                pageCursor = null,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                onlyMedia = false,
                enableCache = false,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            userService.getStatuses(
                id = "userId",
                maxId = null,
                minId = null,
                onlyMedia = false,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                limit = 20,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.user
        }
    }

    @Test
    fun `given next page when getByUser then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.getStatuses(
                id = any(),
                maxId = any(),
                minId = any(),
                onlyMedia = any(),
                excludeReplies = any(),
                excludeReblogs = any(),
                pinned = any(),
                limit = any(),
            )
        } returns (20 downTo 1).map { Status(id = "$it") }

        val res =
            sut.getByUser(
                userId = "userId",
                pageCursor = "0",
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                onlyMedia = false,
                enableCache = false,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("1", res.last().id)
        verifySuspend {
            userService.getStatuses(
                id = "userId",
                maxId = "0",
                minId = null,
                onlyMedia = false,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                limit = 20,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.user
        }
    }

    @Test
    fun `given results when getByUser on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            userService.getStatuses(
                id = any(),
                maxId = any(),
                minId = any(),
                onlyMedia = any(),
                excludeReplies = any(),
                excludeReblogs = any(),
                pinned = any(),
                limit = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getByUser(
                userId = "userId",
                pageCursor = null,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                onlyMedia = false,
                enableCache = false,
                refresh = true,
                otherInstance = otherInstance,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            userService.getStatuses(
                id = "userId",
                maxId = null,
                minId = null,
                onlyMedia = false,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                limit = 20,
            )
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.user
        }
        verify(VerifyMode.not) {
            provider.user
        }
    }

    @Test
    fun `given cached results when getByUser and enableCache then result is from cache`() = runTest {
        // first call to populate cache
        everySuspend {
            userService.getStatuses(
                id = any(),
                maxId = any(),
                minId = any(),
                onlyMedia = any(),
                excludeReplies = any(),
                excludeReblogs = any(),
                pinned = any(),
                limit = any(),
            )
        } returns listOf(Status(id = "1"))

        sut.getByUser(
            userId = "userId",
            pageCursor = null,
            excludeReplies = false,
            excludeReblogs = false,
            pinned = false,
            onlyMedia = false,
            enableCache = true,
            refresh = true,
        )

        // second call with enableCache = true
        val res = sut.getByUser(
            userId = "userId",
            pageCursor = null,
            excludeReplies = false,
            excludeReblogs = false,
            pinned = false,
            onlyMedia = false,
            enableCache = true,
            refresh = false,
        )

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
        assertEquals(listOf(res[0]), sut.getCachedByUser())

        // verify userService was called only once (for the first call)
        verifySuspend(VerifyMode.exactly(1)) {
            userService.getStatuses(
                id = "userId",
                maxId = null,
                minId = null,
                onlyMedia = false,
                excludeReplies = false,
                excludeReblogs = false,
                pinned = false,
                limit = 20,
            )
        }
    }
    // endregion

    // region getById
    @Test
    fun `given error when getById then result and interactions are as expected`() = runTest {
        everySuspend {
            statusService.get(id = any())
        } throws IOException("Network error")

        val res = sut.getById(id = "1")

        assertNull(res)
        verifySuspend {
            statusService.get(id = "1")
        }
    }

    @Test
    fun `given result when getById then result and interactions are as expected`() = runTest {
        everySuspend {
            statusService.get(id = any())
        } returns Status(id = "1")

        val res = sut.getById(id = "1")

        assertNotNull(res)
        assertEquals("1", res.id)
        verifySuspend {
            statusService.get(id = "1")
        }
    }
    // endregion

    // region getSource
    @Test
    fun `given error when getSource then result is null`() = runTest {
        everySuspend { statusService.getSource(any()) } throws IOException()
        assertNull(sut.getSource("1"))
    }

    @Test
    fun `given result when getSource then result is expected`() = runTest {
        everySuspend { statusService.getSource("1") } returns StatusSource(id = "1", text = "source")
        val res = sut.getSource("1")
        assertNotNull(res)
        assertEquals("1", res.id)
        assertEquals("source", res.content)
    }
    // endregion

    // region getContext
    @Test
    fun `given error when getContext then result is null`() = runTest {
        everySuspend { statusService.getContext(any()) } throws IOException()
        assertNull(sut.getContext("1"))
    }

    @Test
    fun `given result when getContext then result is expected`() = runTest {
        everySuspend { statusService.getContext("1") } returns StatusContext(
            ancestors = listOf(Status(id = "0")),
            descendants = listOf(Status(id = "2")),
        )
        val res = sut.getContext("1")
        assertNotNull(res)
        assertEquals(1, res.ancestors.size)
        assertEquals("0", res.ancestors[0].id)
        assertEquals(1, res.descendants.size)
        assertEquals("2", res.descendants[0].id)
    }
    // endregion

    // region Actions
    @Test
    fun `given success when favorite then result and interactions are as expected`() = runTest {
        everySuspend {
            statusService.favorite(id = any())
        } returns Status(id = "1", favourited = true)

        val res = sut.favorite(id = "1")

        assertNotNull(res)
        assertTrue(res.favorite)
        verifySuspend {
            statusService.favorite(id = "1")
        }
    }

    @Test
    fun `given success when reblog then result and interactions are as expected`() = runTest {
        everySuspend {
            statusService.reblog(id = any(), data = any())
        } returns Status(id = "2", reblogged = true)

        val res = sut.reblog(id = "1")

        assertNotNull(res)
        assertTrue(res.reblogged)
        verifySuspend {
            statusService.reblog(id = "1", data = any())
        }
    }

    @Test
    fun `given success when unreblog then result and interactions are as expected`() = runTest {
        everySuspend { statusService.unreblog(id = any()) } returns Status(id = "1", reblogged = false)
        val res = sut.unreblog(id = "1")
        assertNotNull(res)
        verifySuspend { statusService.unreblog(id = "1") }
    }

    @Test
    fun `given success when pin then result and interactions are as expected`() = runTest {
        everySuspend { statusService.pin(id = any()) } returns Status(id = "1", pinned = true)
        val res = sut.pin(id = "1")
        assertNotNull(res)
        verifySuspend { statusService.pin(id = "1") }
    }

    @Test
    fun `given success when unpin then result and interactions are as expected`() = runTest {
        everySuspend { statusService.unpin(id = any()) } returns Status(id = "1", pinned = false)
        val res = sut.unpin(id = "1")
        assertNotNull(res)
        verifySuspend { statusService.unpin(id = "1") }
    }

    @Test
    fun `given success when unfavorite then result and interactions are as expected`() = runTest {
        everySuspend { statusService.unfavorite(id = any()) } returns Status(id = "1", favourited = false)
        val res = sut.unfavorite(id = "1")
        assertNotNull(res)
        verifySuspend { statusService.unfavorite(id = "1") }
    }

    @Test
    fun `given success when bookmark then result and interactions are as expected`() = runTest {
        everySuspend { statusService.bookmark(id = any()) } returns Status(id = "1", bookmarked = true)
        val res = sut.bookmark(id = "1")
        assertNotNull(res)
        verifySuspend { statusService.bookmark(id = "1") }
    }

    @Test
    fun `given success when unbookmark then result and interactions are as expected`() = runTest {
        everySuspend { statusService.unbookmark(id = any()) } returns Status(id = "1", bookmarked = false)
        val res = sut.unbookmark(id = "1")
        assertNotNull(res)
        verifySuspend { statusService.unbookmark(id = "1") }
    }

    @Test
    fun `given success when delete then result and interactions are as expected`() = runTest {
        everySuspend {
            statusService.delete(id = any())
        } returns true

        val res = sut.delete(id = "1")

        assertTrue(res)
        verifySuspend {
            statusService.delete(id = "1")
        }
    }

    @Test
    fun `given success when dislike then result and interactions are as expected`() = runTest {
        everySuspend { statusService.dislike(data = any()) } returns true
        val res = sut.dislike(id = "1")
        assertTrue(res)
        verifySuspend { statusService.dislike(data = any()) }
    }

    @Test
    fun `given success when undislike then result and interactions are as expected`() = runTest {
        everySuspend { statusService.undislike(data = any()) } returns true
        val res = sut.undislike(id = "1")
        assertTrue(res)
        verifySuspend { statusService.undislike(data = any()) }
    }
    // endregion

    // region Favorites and Bookmarks
    @Test
    fun `given results when getFavorites then result is expected`() = runTest {
        everySuspend { userService.getFavorites(maxId = any(), limit = any()) } returns listOf(Status(id = "1"))
        val res = sut.getFavorites(null)
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }

    @Test
    fun `given results when getBookmarks then result is expected`() = runTest {
        everySuspend { userService.getBookmarks(maxId = any(), limit = any()) } returns listOf(Status(id = "1"))
        val res = sut.getBookmarks(null)
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }
    // endregion

    // region Users interactions
    @Test
    fun `given results when getUsersWhoFavorited then result is expected`() = runTest {
        everySuspend {
            statusService.getFavoritedBy(
                id = any(),
                maxId = any(),
                limit = any(),
            )
        } returns listOf(Account(id = "1", acct = "user", username = "user"))
        val res = sut.getUsersWhoFavorited("1", null)
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }

    @Test
    fun `given results when getUsersWhoReblogged then result is expected`() = runTest {
        everySuspend {
            statusService.getRebloggedBy(
                id = any(),
                maxId = any(),
                limit = any(),
            )
        } returns listOf(Account(id = "1", acct = "user", username = "user"))
        val res = sut.getUsersWhoReblogged("1", null)
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }
    // endregion

    // region create
    @Test
    fun `given success when create then result and interactions are as expected`() = runTest {
        everySuspend { statusService.create(key = any(), data = any()) } returns Status(id = "1", content = "text")

        val res = sut.create(
            localId = "localId",
            text = "text",
            title = null,
            spoilerText = null,
            inReplyTo = null,
            sensitive = false,
            mediaIds = null,
            visibility = Visibility.Public,
            lang = null,
            scheduled = null,
            pollOptions = null,
            pollExpirationDate = null,
            pollMultiple = null,
        )

        assertNotNull(res)
        assertEquals("1", res.id)
        assertEquals("text", res.content)
        verifySuspend { statusService.create(key = "localId", data = any()) }
    }
    // endregion

    // region update
    @Test
    fun `given success when update then result and interactions are as expected`() = runTest {
        everySuspend { statusService.update(id = any(), data = any()) } returns Status(id = "1", content = "updated")

        val res = sut.update(
            id = "1",
            text = "updated",
            title = null,
            spoilerText = null,
            inReplyTo = null,
            sensitive = false,
            mediaIds = null,
            visibility = Visibility.Public,
            lang = null,
            pollOptions = null,
            pollExpirationDate = null,
            pollMultiple = null,
        )

        assertNotNull(res)
        assertEquals("1", res.id)
        assertEquals("updated", res.content)
        verifySuspend { statusService.update(id = "1", data = any()) }
    }
    // endregion

    // region submitPoll
    @Test
    fun `given success when submitPoll then result and interactions are as expected`() = runTest {
        everySuspend { pollService.vote(id = any(), data = any()) } returns Poll(id = "1", voted = true)

        val res = sut.submitPoll("1", listOf(0))

        assertNotNull(res)
        assertTrue(res.voted)
        verifySuspend { pollService.vote(id = "1", data = any()) }
    }
    // endregion
}
