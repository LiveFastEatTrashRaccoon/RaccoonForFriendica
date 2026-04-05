package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.PollService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.UserService
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
    // endregion
}
