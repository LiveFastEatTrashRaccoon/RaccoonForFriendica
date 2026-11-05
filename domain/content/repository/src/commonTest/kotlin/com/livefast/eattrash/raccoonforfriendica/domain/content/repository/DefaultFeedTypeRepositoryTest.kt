package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.FeedTypeDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.FeedTypeEntity
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FeedTypeModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultFeedTypeRepositoryTest {
    private val dao = mock<FeedTypeDao>(MockMode.autoUnit)

    private val sut = DefaultFeedTypeRepository(dao = dao)

    @Test
    fun `when getAll then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val entity =
            FeedTypeEntity(
                id = 0,
                accountId = accountId,
                type = TimelineType.Local.toInt(),
            )
        everySuspend { dao.getAll(accountId = any()) } returns listOf(entity)
        val expected =
            FeedTypeModel(
                id = 0,
                accountId = accountId,
                type = TimelineType.Local,
            )

        val result = sut.getAll(accountId)

        assertEquals(1, result.size)
        assertEquals(expected, result.first())
        verifySuspend {
            dao.getAll(accountId = accountId)
        }
    }

    @Test
    fun `when getAllAsFlow then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val entity =
            FeedTypeEntity(
                id = 0,
                accountId = accountId,
                type = TimelineType.Local.toInt(),
            )
        every { dao.getAllAsFlow(any()) } returns flowOf(listOf(entity))
        val expected =
            FeedTypeModel(
                id = 0,
                accountId = accountId,
                type = TimelineType.Local,
            )

        sut.getAllAsFlow(accountId).test {
            val res = awaitItem()
            assertEquals(listOf(expected), res)
            cancelAndIgnoreRemainingEvents()
        }
        verify {
            dao.getAllAsFlow(accountId = accountId)
        }
    }

    @Test
    fun `given info is null when getBy then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val type = TimelineType.Local.toInt()
        val entity =
            FeedTypeEntity(
                id = 0,
                accountId = accountId,
                type = type,
            )
        everySuspend { dao.getBy(accountId = any(), type = any()) } returns entity
        val expected =
            FeedTypeModel(
                id = 0,
                accountId = accountId,
                type = TimelineType.Local,
            )

        val result = sut.getBy(accountId = accountId, type = type, info = null)

        assertEquals(expected, result)
        verifySuspend {
            dao.getBy(accountId = accountId, type = type)
        }
    }

    @Test
    fun `given info is not null when getBy then result and interactions are as expected`() = runTest {
        val accountId = 1L
        val type = 3
        val info = "custom-circle"
        val entity =
            FeedTypeEntity(
                id = 0,
                accountId = accountId,
                type = type,
                info = info,
            )
        everySuspend { dao.getBy(accountId = any(), type = any(), info = any()) } returns entity
        val expected =
            FeedTypeModel(
                id = 0,
                accountId = accountId,
                type = TimelineType.Circle(),
                info = info,
            )

        val result = sut.getBy(accountId = accountId, type = type, info = info)

        assertEquals(expected, result)
        verifySuspend {
            dao.getBy(accountId = accountId, type = type, info = info)
        }
    }

    @Test
    fun `given null entity when getBy then result is null`() = runTest {
        val accountId = 1L
        val type = TimelineType.Local.toInt()
        everySuspend { dao.getBy(accountId = any(), type = any()) } returns null

        val result = sut.getBy(accountId = accountId, type = type, info = null)

        assertNull(result)
        verifySuspend {
            dao.getBy(accountId = accountId, type = type)
        }
    }

    @Test
    fun `when create single item then interactions are as expected`() = runTest {
        val model =
            FeedTypeModel(
                id = 0,
                accountId = 1L,
                type = TimelineType.Local,
                index = 2,
                info = "info",
            )
        val expectedEntity =
            FeedTypeEntity(
                id = 0,
                accountId = 1L,
                type = TimelineType.Local.toInt(),
                index = 2,
                info = "info",
            )

        sut.create(model)

        verifySuspend {
            dao.insert(expectedEntity)
        }
    }

    @Test
    fun `when create multiple items then interactions are as expected`() = runTest {
        val models =
            listOf(
                FeedTypeModel(
                    id = 0,
                    accountId = 1L,
                    type = TimelineType.Local,
                    index = 0,
                ),
                FeedTypeModel(
                    id = 1,
                    accountId = 1L,
                    type = TimelineType.Subscriptions,
                    index = 1,
                ),
            )
        val expectedEntities =
            arrayOf(
                FeedTypeEntity(
                    id = 0,
                    accountId = 1L,
                    type = TimelineType.Local.toInt(),
                    index = 0,
                ),
                FeedTypeEntity(
                    id = 1,
                    accountId = 1L,
                    type = TimelineType.Subscriptions.toInt(),
                    index = 1,
                ),
            )

        sut.create(models)

        verifySuspend {
            dao.insertMany(*expectedEntities)
        }
    }

    @Test
    fun `when update single item then interactions are as expected`() = runTest {
        val model =
            FeedTypeModel(
                id = 0,
                accountId = 1L,
                type = TimelineType.Local,
                index = 3,
            )
        val expectedEntity =
            FeedTypeEntity(
                id = 0,
                accountId = 1L,
                type = TimelineType.Local.toInt(),
                index = 3,
            )

        sut.update(model)

        verifySuspend {
            dao.update(expectedEntity)
        }
    }

    @Test
    fun `when update multiple items then interactions are as expected`() = runTest {
        val models =
            listOf(
                FeedTypeModel(
                    id = 0,
                    accountId = 1L,
                    type = TimelineType.Local,
                    index = 0,
                ),
                FeedTypeModel(
                    id = 1,
                    accountId = 1L,
                    type = TimelineType.Subscriptions,
                    index = 1,
                ),
            )
        val expectedEntities =
            arrayOf(
                FeedTypeEntity(
                    id = 0,
                    accountId = 1L,
                    type = TimelineType.Local.toInt(),
                    index = 0,
                ),
                FeedTypeEntity(
                    id = 1,
                    accountId = 1L,
                    type = TimelineType.Subscriptions.toInt(),
                    index = 1,
                ),
            )

        sut.update(models)

        verifySuspend {
            dao.updateMany(*expectedEntities)
        }
    }

    @Test
    fun `when delete then interactions are as expected`() = runTest {
        val model =
            FeedTypeModel(
                id = 0,
                accountId = 1L,
                type = TimelineType.Local,
            )
        val expectedEntity =
            FeedTypeEntity(
                id = 0,
                accountId = 1L,
                type = TimelineType.Local.toInt(),
            )

        sut.delete(model)

        verifySuspend {
            dao.delete(expectedEntity)
        }
    }
}
