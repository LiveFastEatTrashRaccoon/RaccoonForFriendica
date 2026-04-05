package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.TrendsService
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultTrendingRepositoryTest {
    private val trendsService = mock<TrendsService>()
    private val json = Json { ignoreUnknownKeys = true }

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
            json = json,
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
    // endregion

    // region getLinks
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
