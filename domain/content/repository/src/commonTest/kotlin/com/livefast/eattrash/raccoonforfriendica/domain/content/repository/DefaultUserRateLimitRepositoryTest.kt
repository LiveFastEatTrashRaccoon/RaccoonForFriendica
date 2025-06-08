package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.UserRateLimitDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.UserRateLimitEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserRateLimitModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultUserRateLimitRepositoryTest {
    private val userRateLimitDao = mock<UserRateLimitDao>(MockMode.autoUnit)
    private val sut =
        DefaultUserRateLimitRepository(
            userRateLimitDao = userRateLimitDao,
        )

    @Test
    fun `given no results when getAll then result and interactions are as expected`() = runTest {
        val accountId = 1L
        everySuspend { userRateLimitDao.getAll(any()) } returns emptyList()

        val res = sut.getAll(accountId)

        assertEquals(emptyList(), res)
        verifySuspend {
            userRateLimitDao.getAll(accountId)
        }
    }

    @Test
    fun `given results when getAll then result and interactions are as expected`() = runTest {
        val accountId = 1L
        everySuspend { userRateLimitDao.getAll(any()) } returns
            listOf(
                UserRateLimitEntity(id = 1L, userHandle = "user-1"),
                UserRateLimitEntity(id = 2L, userHandle = "user-2"),
            )

        val res = sut.getAll(accountId)

        assertEquals(2, res.size)
        assertEquals(UserRateLimitModel(id = 1L, handle = "user-1"), res.first())
        assertEquals(UserRateLimitModel(id = 2L, handle = "user-2"), res[1])
        verifySuspend {
            userRateLimitDao.getAll(accountId)
        }
    }

    @Test
    fun `when getBy then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val handle = "user-1"
        everySuspend { userRateLimitDao.getBy(any(), any()) } returns
            UserRateLimitEntity(id = 1L, userHandle = handle)

        val res = sut.getBy(accountId = accountId, handle = handle)

        assertEquals(UserRateLimitModel(id = 1L, handle = handle), res)
        verifySuspend {
            userRateLimitDao.getBy(accountId = accountId, handle = handle)
        }
    }

    @Test
    fun `when create then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val id = 2L
        val handle = "user-1"
        everySuspend { userRateLimitDao.getBy(any(), any()) } returns
            UserRateLimitEntity(id = id, userHandle = handle, accountId = accountId)
        val model = UserRateLimitModel(id = id, accountId = accountId, handle = handle)

        val res = sut.create(model)

        assertEquals(model, res)
        verifySuspend {
            userRateLimitDao.insert(
                UserRateLimitEntity(
                    id = id,
                    accountId = accountId,
                    userHandle = handle,
                ),
            )
            userRateLimitDao.getBy(accountId = accountId, handle = handle)
        }
    }

    @Test
    fun `when update then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val id = 2L
        val handle = "user-1"
        everySuspend { userRateLimitDao.getBy(any(), any()) } returns
            UserRateLimitEntity(id = id, userHandle = handle, accountId = accountId)
        val model = UserRateLimitModel(id = id, accountId = accountId, handle = handle)

        val res = sut.update(model)

        assertEquals(model, res)
        verifySuspend {
            userRateLimitDao.update(
                UserRateLimitEntity(
                    id = id,
                    accountId = accountId,
                    userHandle = handle,
                ),
            )
            userRateLimitDao.getBy(accountId = accountId, handle = handle)
        }
    }

    @Test
    fun `when delete then result and interactions are as expected`() = runTest {
        val id = 1L

        val res = sut.delete(id)

        assertTrue(res)
        verifySuspend {
            userRateLimitDao.delete(UserRateLimitEntity(id = id, userHandle = ""))
        }
    }
}
