package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.FollowRequestService
import com.livefast.eattrash.raccoonforfriendica.core.api.service.SearchService
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

class DefaultUserRepositoryTest {
    private val userService = mock<UserService>()
    private val searchService = mock<SearchService>()
    private val followRequestService = mock<FollowRequestService>()

    private val provider =
        mock<ServiceProvider> {
            every { user } returns userService
            every { search } returns searchService
            every { followRequest } returns followRequestService
        }

    private val otherProvider =
        mock<ServiceProvider>(MockMode.autoUnit) {
            every { user } returns userService
            every { search } returns searchService
            every { followRequest } returns followRequestService
        }

    private val sut =
        DefaultUserRepository(
            provider = provider,
            otherProvider = otherProvider,
        )

    // region getById
    @Test
    fun `given error when getById then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.getById(any())
        } throws IOException("Network error")

        val res = sut.getById("1")

        assertNull(res)
        verifySuspend {
            userService.getById("1")
        }
    }

    @Test
    fun `given result when getById then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.getById(any())
        } returns Account(id = "1", username = "user", acct = "user@example.com")

        val res = sut.getById("1")

        assertNotNull(res)
        assertEquals("1", res.id)
        verifySuspend {
            userService.getById("1")
        }
    }
    // endregion

    // region search
    @Test
    fun `given results when search then result and interactions are as expected`() = runTest {
        everySuspend {
            userService.search(
                query = any(),
                offset = any(),
                resolve = any(),
                following = any(),
            )
        } returns listOf(Account(id = "1", username = "user", acct = "user@example.com"))

        val res = sut.search(query = "query", offset = 0, following = false)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res.first().id)
        verifySuspend {
            userService.search(
                query = "query",
                offset = 0,
                resolve = true,
                following = false,
            )
        }
    }
    // endregion

    // region getFollowers
    @Test
    fun `given results when getFollowers on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            userService.getFollowers(
                id = any(),
                maxId = any(),
                limit = any(),
            )
        } returns listOf(Account(id = "2", username = "follower", acct = "follower@node"))

        val res = sut.getFollowers(id = "1", pageCursor = null, otherInstance = otherInstance)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("2", res.first().id)
        verifySuspend {
            userService.getFollowers(
                id = "1",
                maxId = null,
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
}
