package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Search
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Suggestion
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
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

    @Test
    fun `given results when getByHandle then result is expected`() = runTest {
        everySuspend {
            userService.search(query = any(), resolve = any())
        } returns listOf(Account(id = "1", username = "user", acct = "handle"))
        val res = sut.getByHandle("handle")
        assertNotNull(res)
        assertEquals("1", res.id)
    }

    @Test
    fun `given results when searchMyFollowing then result is expected`() = runTest {
        everySuspend {
            searchService.search(
                query = any(),
                type = any(),
                maxId = any(),
                following = any(),
                limit = any(),
            )
        } returns Search(
            accounts = listOf(Account(id = "1", username = "user", acct = "acct")),
        )
        val res = sut.searchMyFollowing("query", null)
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }
    // endregion

    // region getCurrent
    @Test
    fun `given cached user when getCurrent then return cached user`() = runTest {
        everySuspend { userService.verifyCredentials() } returns
            CredentialAccount(id = "1", username = "user", acct = "handle")

        val res1 = sut.getCurrent(refresh = true)
        val res2 = sut.getCurrent(refresh = false)

        assertEquals(res1, res2)
        verifySuspend(VerifyMode.exactly(1)) { userService.verifyCredentials() }
    }
    // endregion

    // region followers & following
    @Test
    fun `given results when getFollowing on other instance then result and interactions are as expected`() = runTest {
        val otherInstance = "node"
        everySuspend {
            userService.getFollowing(
                id = any(),
                maxId = any(),
                limit = any(),
            )
        } returns listOf(Account(id = "2", username = "following", acct = "following@node"))

        val res = sut.getFollowing(id = "1", pageCursor = null, otherInstance = otherInstance)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("2", res.first().id)
        verifySuspend {
            userService.getFollowing(
                id = "1",
                maxId = null,
                limit = 20,
            )
        }
        verify {
            otherProvider.changeNode(otherInstance)
            otherProvider.user
        }
    }

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

    // region actions
    @Test
    fun `given results when getRelationships then result is expected`() = runTest {
        everySuspend { userService.getRelationships(any()) } returns listOf(Relationship(id = "1"))
        val res = sut.getRelationships(listOf("1"))
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }

    @Test
    fun `given results when getSuggestions then result is expected`() = runTest {
        everySuspend { userService.getSuggestions(any()) } returns listOf(
            Suggestion(user = Account(id = "1", username = "user", acct = "acct")),
        )
        val res = sut.getSuggestions()
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }

    @Test
    fun `given results when getListsContaining then result is expected`() = runTest {
        everySuspend { userService.getListsContaining(any()) } returns listOf(UserList(id = "1", title = "list"))
        val res = sut.getListsContaining("1")
        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res[0].id)
    }

    @Test
    fun `given success when follow then result is expected`() = runTest {
        everySuspend { userService.follow(id = any(), data = any()) } returns Relationship(id = "1", following = true)
        val res = sut.follow("1", reblogs = true, notifications = true)
        assertNotNull(res)
        assertEquals(true, res.following)
    }

    @Test
    fun `given success when unfollow then result is expected`() = runTest {
        everySuspend { userService.unfollow(any()) } returns Relationship(id = "1", following = false)
        val res = sut.unfollow("1")
        assertNotNull(res)
    }

    @Test
    fun `given success when mute then result is expected`() = runTest {
        everySuspend { userService.mute(id = any(), data = any()) } returns Relationship(id = "1", muting = true)
        val res = sut.mute("1", 0L, true)
        assertNotNull(res)
        assertEquals(true, res.muting)
    }

    @Test
    fun `given success when unmute then result is expected`() = runTest {
        everySuspend { userService.unmute(any()) } returns Relationship(id = "1", muting = false)
        val res = sut.unmute("1")
        assertNotNull(res)
    }

    @Test
    fun `given success when block then result is expected`() = runTest {
        everySuspend { userService.block(any()) } returns Relationship(id = "1", blocking = true)
        val res = sut.block("1")
        assertNotNull(res)
    }

    @Test
    fun `given success when unblock then result is expected`() = runTest {
        everySuspend { userService.unblock(any()) } returns Relationship(id = "1", blocking = false)
        val res = sut.unblock("1")
        assertNotNull(res)
    }

    @Test
    fun `given results when getMuted then result is expected`() = runTest {
        everySuspend { userService.getMuted(any(), any()) } returns
            listOf(Account(id = "1", username = "u", acct = "a"))
        val res = sut.getMuted(null)
        assertNotNull(res)
        assertEquals(1, res.size)
    }

    @Test
    fun `given results when getBlocked then result is expected`() = runTest {
        everySuspend { userService.getBlocked(any(), any()) } returns
            listOf(Account(id = "1", username = "u", acct = "a"))
        val res = sut.getBlocked(null)
        assertNotNull(res)
        assertEquals(1, res.size)
    }
    // endregion

    // region Follow Requests
    @Test
    fun `given results when getFollowRequests then result is expected`() = runTest {
        everySuspend { followRequestService.getAll(any(), any()) } returns
            (listOf(Account(id = "1", username = "u", acct = "a")) to "cursor")
        val res = sut.getFollowRequests(null)
        assertNotNull(res)
        assertEquals(1, res.list.size)
        assertEquals("cursor", res.cursor)
    }

    @Test
    fun `given success when acceptFollowRequest then return true`() = runTest {
        everySuspend { followRequestService.accept(any()) } returns Relationship(id = "1")
        assertEquals(true, sut.acceptFollowRequest("1"))
    }

    @Test
    fun `given success when rejectFollowRequest then return true`() = runTest {
        everySuspend { followRequestService.reject(any()) } returns Relationship(id = "1")
        assertEquals(true, sut.rejectFollowRequest("1"))
    }
    // endregion

    // region updateProfile
    @Test
    fun `given success when updateProfile then result is expected`() = runTest {
        everySuspend { userService.updateProfile(any()) } returns Account(id = "1", username = "user", acct = "acct")
        val res = sut.updateProfile(
            note = "note",
            displayName = "name",
            avatar = null,
            header = null,
            locked = null,
            bot = null,
            discoverable = null,
            hideCollections = null,
            indexable = null,
            fields = null,
        )
        assertNotNull(res)
        verifySuspend { userService.updateProfile(any()) }
    }

    @Test
    fun `given images when updateProfile then image update service is called`() = runTest {
        everySuspend { userService.updateProfile(any()) } returns Account(id = "1", username = "user", acct = "acct")
        everySuspend { userService.updateProfileImage(any()) } returns
            Account(id = "1", username = "user", acct = "acct")

        val res = sut.updateProfile(
            note = "note",
            displayName = "name",
            avatar = byteArrayOf(1, 2, 3),
            header = byteArrayOf(4, 5, 6),
            locked = null,
            bot = null,
            discoverable = null,
            hideCollections = null,
            indexable = null,
            fields = null,
        )

        assertNotNull(res)
        verifySuspend(VerifyMode.exactly(1)) { userService.updateProfile(any()) }
        verifySuspend(VerifyMode.exactly(2)) { userService.updateProfileImage(any()) }
    }

    @Test
    fun `given success when updatePersonalNote then result is expected`() = runTest {
        everySuspend { userService.updatePersonalNote(any(), any()) } returns Relationship(id = "1", note = "note")
        val res = sut.updatePersonalNote("1", "note")
        assertNotNull(res)
        assertEquals("note", res.note)
    }
    // endregion
}
