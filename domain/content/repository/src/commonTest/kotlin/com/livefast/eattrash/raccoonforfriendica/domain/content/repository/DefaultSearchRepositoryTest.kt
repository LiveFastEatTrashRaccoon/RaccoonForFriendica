package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Tag
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.SearchService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultSearchRepositoryTest {
    private val searchService = mock<SearchService>()
    private val provider = mock<ServiceProvider> {
        every { search } returns searchService
    }
    private val sut = DefaultSearchRepository(provider)

    @Test
    fun `given error when search then return null`() = runTest {
        everySuspend {
            searchService.search(
                query = any(),
                maxId = any(),
                limit = any(),
                type = any(),
                resolve = any(),
            )
        } throws IOException("Network error")

        val result = sut.search("query", SearchResultType.Entries)

        assertNull(result)
    }

    @Test
    fun `given entries type when search then return entries`() = runTest {
        everySuspend {
            searchService.search(
                query = any(),
                maxId = any(),
                limit = any(),
                type = any(),
                resolve = any(),
            )
        } returns Search(
            statuses = listOf(Status(id = "1", content = "content")),
        )

        val result = sut.search("query", SearchResultType.Entries)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertIs<ExploreItemModel.Entry>(result.first())
        assertEquals("1", result.first().id)
        verifySuspend {
            searchService.search(
                query = "query",
                maxId = null,
                limit = 20,
                type = "statuses",
                resolve = false,
            )
        }
    }

    @Test
    fun `given hashtags type when search then return hashtags`() = runTest {
        everySuspend {
            searchService.search(
                query = any(),
                maxId = any(),
                limit = any(),
                type = any(),
                resolve = any(),
            )
        } returns Search(
            hashtags = listOf(Tag(name = "tag", url = "url")),
        )

        val result = sut.search("query", SearchResultType.Hashtags)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertIs<ExploreItemModel.HashTag>(result.first())
        assertEquals("tag", result.first().id)
        verifySuspend {
            searchService.search(
                query = "query",
                maxId = null,
                limit = 20,
                type = "hashtags",
                resolve = false,
            )
        }
    }

    @Test
    fun `given users type when search then return users`() = runTest {
        everySuspend {
            searchService.search(
                query = any(),
                maxId = any(),
                limit = any(),
                type = any(),
                resolve = any(),
            )
        } returns Search(
            accounts = listOf(Account(id = "1", username = "user", acct = "acct")),
        )

        val result = sut.search("query", SearchResultType.Users)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertIs<ExploreItemModel.User>(result.first())
        assertEquals("1", result.first().id)
        verifySuspend {
            searchService.search(
                query = "query",
                maxId = null,
                limit = 20,
                type = "accounts",
                resolve = false,
            )
        }
    }
}
