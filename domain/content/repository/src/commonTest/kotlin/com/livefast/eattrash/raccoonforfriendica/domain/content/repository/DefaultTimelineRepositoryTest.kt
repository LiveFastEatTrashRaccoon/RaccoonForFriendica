package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TimelineService
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

class DefaultTimelineRepositoryTest {
    private val timelineService = mock<TimelineService>()
    private val provider =
        mock<ServiceProvider> {
            every { timeline } returns timelineService
        }
    private val otherProvider =
        mock<ServiceProvider>(MockMode.autoUnit) {
            every { timeline } returns timelineService
        }
    private val sut =
        DefaultTimelineRepository(
            provider = provider,
            otherProvider = otherProvider,
        )

    // region Public
    @Test
    fun `given error when getPublic then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } throws IOException("Network error")

        val res =
            sut.getPublic(
                pageCursor = null,
                refresh = true,
            )

        assertNull(res)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = false,
            )
        }
    }

    @Test
    fun `given no results when getPublic then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns emptyList()

        val res =
            sut.getPublic(
                pageCursor = null,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(emptyList(), res)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = false,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given results when getPublic then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getPublic(
                pageCursor = null,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = false,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given next page when getPublic then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns (20 downTo 1).map { Status(id = "$it") }

        val res =
            sut.getPublic(
                pageCursor = "0",
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("1", res.last().id)
        verifySuspend {
            timelineService.getPublic(
                maxId = "0",
                minId = null,
                limit = 20,
                local = false,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given cached results when getPublic and not refresh then return cached`() = runTest {
        everySuspend {
            timelineService.getPublic(any(), any(), any(), any())
        } returns listOf(Status(id = "1"))
        sut.getPublic(null, refresh = true)

        val res = sut.getPublic(null, refresh = false)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
        verifySuspend(VerifyMode.exactly(1)) {
            timelineService.getPublic(any(), any(), any(), any())
        }
    }
    // endregion

    // region Home
    @Test
    fun `given error when getHome then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getHome(
                maxId = any(),
                minId = any(),
                limit = any(),
            )
        } throws IOException("Network error")

        val res =
            sut.getHome(
                pageCursor = null,
                refresh = true,
            )

        assertNull(res)
        verifySuspend {
            timelineService.getHome(
                maxId = null,
                minId = null,
                limit = 20,
            )
        }
    }

    @Test
    fun `given no results when getHome then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getHome(
                maxId = any(),
                minId = any(),
                limit = any(),
            )
        } returns emptyList()

        val res =
            sut.getHome(
                pageCursor = null,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(emptyList(), res)
        verifySuspend {
            timelineService.getHome(
                maxId = null,
                minId = null,
                limit = 20,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given results when getHome then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getHome(
                maxId = any(),
                minId = any(),
                limit = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getHome(
                pageCursor = null,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            timelineService.getHome(
                maxId = null,
                minId = null,
                limit = 20,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given next page when getHome then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getHome(
                maxId = any(),
                minId = any(),
                limit = any(),
            )
        } returns (20 downTo 1).map { Status(id = "$it") }

        val res =
            sut.getHome(
                pageCursor = "0",
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("1", res.last().id)
        verifySuspend {
            timelineService.getHome(
                maxId = "0",
                minId = null,
                limit = 20,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }
    // endregion

    // region Local
    @Test
    fun `given error when getLocal then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } throws IOException("Network error")

        val res =
            sut.getLocal(
                pageCursor = null,
                refresh = true,
            )

        assertNull(res)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = true,
            )
        }
    }

    @Test
    fun `given no results when getLocal then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns emptyList()

        val res =
            sut.getLocal(
                pageCursor = null,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(emptyList(), res)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = true,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given results when getLocal then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getLocal(
                pageCursor = null,
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = true,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given next page when getLocal then result and interactions are as expected`() = runTest {
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns (20 downTo 1).map { Status(id = "$it") }

        val res =
            sut.getLocal(
                pageCursor = "0",
                refresh = true,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("1", res.last().id)
        verifySuspend {
            timelineService.getPublic(
                maxId = "0",
                minId = null,
                limit = 20,
                local = true,
            )
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given no results when getLocal on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns emptyList()

        val res =
            sut.getLocal(
                pageCursor = null,
                refresh = true,
                otherInstance = otherInstance,
            )

        assertNotNull(res)
        assertEquals(emptyList(), res)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = true,
            )
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.timeline
        }
        verify(VerifyMode.not) {
            provider.timeline
        }
    }

    @Test
    fun `given results when getLocal on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getLocal(
                pageCursor = null,
                refresh = true,
                otherInstance = otherInstance,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            timelineService.getPublic(
                maxId = null,
                minId = null,
                limit = 20,
                local = true,
            )
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.timeline
        }
        verify(VerifyMode.not) {
            provider.timeline
        }
    }

    @Test
    fun `given next page when getLocal on foreign instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            timelineService.getPublic(
                maxId = any(),
                minId = any(),
                limit = any(),
                local = any(),
            )
        } returns (20 downTo 1).map { Status(id = "$it") }

        val res =
            sut.getLocal(
                pageCursor = "0",
                refresh = true,
                otherInstance = otherInstance,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("1", res.last().id)
        verifySuspend {
            timelineService.getPublic(
                maxId = "0",
                minId = null,
                limit = 20,
                local = true,
            )
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.timeline
        }
        verify(VerifyMode.not) {
            provider.timeline
        }
    }
    // endregion

    // region getHashtag
    @Test
    fun `given results when getHashtag then result and interactions are as expected`() = runTest {
        val hashtag = "tag"
        everySuspend {
            timelineService.getHashtag(
                maxId = any(),
                minId = any(),
                limit = any(),
                hashtag = any(),
            )
        } returns ((19 downTo 0).map { Status(id = "$it") } to "42")

        val res =
            sut.getHashtag(
                hashtag = hashtag,
                pageCursor = null,
            )

        assertNotNull(res)
        assertEquals("42", res.cursor)
        assertEquals(20, res.list.size)
        assertEquals("0", res.list.last().id)
        verifySuspend {
            timelineService.getHashtag(
                maxId = null,
                minId = null,
                limit = 20,
                hashtag = hashtag,
            )
        }
        verify {
            provider.timeline
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given results when getHashtag on other instance then result and interactions are as expected`() = runTest {
        val hashtag = "tag"
        val otherInstance = "node"
        everySuspend {
            timelineService.getHashtag(
                maxId = any(),
                minId = any(),
                limit = any(),
                hashtag = any(),
            )
        } returns ((19 downTo 0).map { Status(id = "$it") } to "42")

        val res =
            sut.getHashtag(
                hashtag = hashtag,
                pageCursor = null,
                otherInstance = otherInstance,
            )

        assertNotNull(res)
        assertEquals("42", res.cursor)
        assertEquals(20, res.list.size)
        assertEquals("0", res.list.last().id)
        verifySuspend {
            timelineService.getHashtag(
                maxId = null,
                minId = null,
                limit = 20,
                hashtag = hashtag,
            )
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.timeline
        }
        verify(VerifyMode.not) {
            provider.timeline
        }
    }
    // endregion

    // region getCircle
    @Test
    fun `given results when getCircle then result and interactions are as expected`() = runTest {
        val circleId = "42"
        everySuspend {
            timelineService.getList(
                id = any(),
                maxId = any(),
                limit = any(),
            )
        } returns (19 downTo 0).map { Status(id = "$it") }

        val res =
            sut.getCircle(
                id = circleId,
                pageCursor = null,
            )

        assertNotNull(res)
        assertEquals(20, res.size)
        assertEquals("0", res.last().id)
        verifySuspend {
            timelineService.getList(
                id = circleId,
                maxId = null,
                limit = 20,
            )
        }
        verify {
            provider.timeline
        }
        verify(VerifyMode.not) {
            otherProvider.timeline
        }
    }

    @Test
    fun `given error when getCircle then result is null`() = runTest {
        val circleId = "42"
        everySuspend {
            timelineService.getList(
                id = any(),
                maxId = any(),
                limit = any(),
            )
        } throws IOException("Network error")

        val res =
            sut.getCircle(
                id = circleId,
                pageCursor = null,
            )

        assertNull(res)
        verifySuspend {
            timelineService.getList(
                id = circleId,
                maxId = null,
                limit = 20,
            )
        }
    }
    // endregion
}
