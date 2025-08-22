package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.EditListMembersForm
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ListService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleReplyPolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultCirclesRepositoryTest {
    private val listService = mock<ListService>()
    private val serviceProvider =
        mock<ServiceProvider> {
            every { list } returns listService
        }
    private val sut = DefaultCirclesRepository(provider = serviceProvider)

    @Test
    fun `when getAll then result is as expected`() = runTest {
        val list = listOf(UserList(id = "1"))
        everySuspend { listService.getAll() } returns list

        val res = sut.getAll()

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            listService.getAll()
        }
    }

    @Test
    fun `when get then result is as expected`() = runTest {
        val list = UserList(id = "1")
        everySuspend { listService.getBy(any()) } returns list

        val res = sut.get("1")

        assertEquals(list.toModel(), res)
        verifySuspend {
            listService.getBy("1")
        }
    }

    @Test
    fun `when getMembers then result is as expected`() = runTest {
        val list = listOf(Account(acct = "", id = "", username = ""))
        everySuspend { listService.getMembers(id = any(), maxId = any()) } returns list

        val res = sut.getMembers("1")

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            listService.getMembers(id = "1", maxId = null)
        }
    }

    @Test
    fun `when create then result is as expected`() = runTest {
        val list = UserList(id = "1")
        everySuspend { listService.create(any()) } returns list

        val res =
            sut.create(title = "name", replyPolicy = CircleReplyPolicy.List, exclusive = false)

        assertEquals(list.toModel(), res)
        verifySuspend {
            listService.create(
                EditListForm(
                    title = "name",
                    exclusive = false,
                    replyPolicy = "list",
                ),
            )
        }
    }

    @Test
    fun `when update then result is as expected`() = runTest {
        val list = UserList(id = "1")
        everySuspend { listService.update(id = any(), data = any()) } returns list

        val res =
            sut.update(
                id = "1",
                title = "name",
                replyPolicy = CircleReplyPolicy.Followed,
            )

        assertEquals(list.toModel(), res)
        verifySuspend {
            listService.update(
                "1",
                EditListForm(
                    title = "name",
                    exclusive = false,
                    replyPolicy = "followed",
                ),
            )
        }
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        everySuspend { listService.delete(any()) } returns true

        val res = sut.delete(id = "1")

        assertTrue(res)
        verifySuspend {
            listService.delete("1")
        }
    }

    @Test
    fun `given error when delete then result is as expected`() = runTest {
        everySuspend { listService.delete(any()) } returns false

        val res = sut.delete(id = "1")

        assertFalse(res)
        verifySuspend {
            listService.delete("1")
        }
    }

    @Test
    fun `when addMembers then result is as expected`() = runTest {
        everySuspend { listService.addMembers(any(), any()) } returns true
        val accountIds = listOf("2", "3")

        val res = sut.addMembers("1", accountIds)

        assertTrue(res)
        verifySuspend {
            listService.addMembers(
                id = "1",
                data = EditListMembersForm(accountIds = accountIds),
            )
        }
    }

    @Test
    fun `when removeMembers then result is as expected`() = runTest {
        everySuspend {
            listService.removeMembers(id = any(), data = any())
        } returns true
        val accountIds = listOf("2", "3")

        val res = sut.removeMembers("1", accountIds)

        assertTrue(res)
        verifySuspend {
            listService.removeMembers(
                id = "1",
                data = EditListMembersForm(accountIds = accountIds),
            )
        }
    }
}
