package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.TrendsLink
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsService
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

class DefaultTrendingRepositoryTest {
    private val trendsService = mock<TrendsService>()

    private val provider =
        mock<ServiceProvider> {
            every { trend } returns trendsService
        }

    private val otherProvider =
        mock<ServiceProvider>(MockMode.autoUnit) {
            every { trend } returns trendsService
        }

    private val sut =
        DefaultTrendingRepository(
            provider = provider,
            otherProvider = otherProvider,
        )

    // region getEntries
    @Test
    fun `given error when getEntries then result and interactions are as expected`() = runTest {
        everySuspend {
            trendsService.getStatuses(any(), any())
        } throws IOException("Network error")

        val res = sut.getEntries(offset = 0)

        assertNull(res)
        verifySuspend {
            trendsService.getStatuses(offset = 0, limit = 20)
        }
    }

    @Test
    fun `given results when getEntries then result and interactions are as expected`() = runTest {
        everySuspend {
            trendsService.getStatuses(any(), any())
        } returns listOf(Status(id = "1", content = "trending status"))

        val res = sut.getEntries(offset = 0)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res.first().id)
        verifySuspend {
            trendsService.getStatuses(offset = 0, limit = 20)
        }
    }

    @Test
    fun `given results when getEntries on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            trendsService.getStatuses(any(), any())
        } returns listOf(Status(id = "1", content = "trending status"))

        val res = sut.getEntries(offset = 0, otherInstance = otherInstance)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res.first().id)
        verifySuspend {
            trendsService.getStatuses(offset = 0, limit = 20)
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.trend
        }
        verify(VerifyMode.not) {
            provider.trend
        }
    }
    // endregion

    // region getHashtags
    @Test
    fun `given results when getHashtags then result and interactions are as expected`() = runTest {
        everySuspend {
            trendsService.getHashtags(any(), any())
        } returns listOf(Tag(name = "trending", url = "https://example.com/tag/trending"))

        val res = sut.getHashtags(offset = 0, refresh = true)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("trending", res.first().name)
        verifySuspend {
            trendsService.getHashtags(offset = 0, limit = 20)
        }
    }

    @Test
    fun `given cached results when getHashtags then cached result is returned`() = runTest {
        everySuspend {
            trendsService.getHashtags(any(), any())
        } returns listOf(Tag(name = "trending", url = "https://example.com/tag/trending"))
        // populate cache
        sut.getHashtags(offset = 0, refresh = true)

        val res = sut.getHashtags(offset = 0, refresh = false)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("trending", res.first().name)

        verifySuspend(VerifyMode.exactly(1)) {
            trendsService.getHashtags(any(), any())
        }
    }

    @Test
    fun `given results when getHashtags on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            trendsService.getHashtags(any(), any())
        } returns listOf(Tag(name = "trending", url = "https://example.com/tag/trending"))

        val res = sut.getHashtags(offset = 0, refresh = true, otherInstance = otherInstance)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("trending", res.first().name)
        verifySuspend {
            trendsService.getHashtags(offset = 0, limit = 20)
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.trend
        }
        verify(VerifyMode.not) {
            provider.trend
        }
    }
    // endregion

    // region getLinks
    @Test
    fun `given results when getLinks then result and interactions are as expected`() = runTest {
        val link = TrendsLink(url = "https://example.com", title = "link")
        everySuspend {
            trendsService.getLinks(any(), any())
        } returns listOf(link)

        val res = sut.getLinks(offset = 0)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals(link.title, res.first().title)
        verifySuspend {
            trendsService.getLinks(offset = 0, limit = 20)
        }
        verify {
            provider.trend
        }
        verify(VerifyMode.not) {
            otherProvider.trend
        }
    }

    @Test
    fun `given results when getLinks on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        val link = TrendsLink(url = "https://example.com", title = "link")
        everySuspend {
            trendsService.getLinks(any(), any())
        } returns listOf(link)

        val res = sut.getLinks(offset = 0, otherInstance = otherInstance)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals(link.title, res.first().title)
        verifySuspend {
            trendsService.getLinks(offset = 0, limit = 20)
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.trend
        }
        verify(VerifyMode.not) {
            provider.trend
        }
    }

    @Test
    fun `given error when getLinks then result is null`() = runTest {
        everySuspend {
            trendsService.getLinks(any(), any())
        } throws IOException("Network error")

        val res = sut.getLinks(offset = 0)

        assertNull(res)
    }
    // endregion
}
