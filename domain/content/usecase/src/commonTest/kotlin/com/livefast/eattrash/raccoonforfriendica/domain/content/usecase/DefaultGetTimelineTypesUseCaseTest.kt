package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FeedTypeModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FeedTypeRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGetTimelineTypesUseCaseTest {
    private val accountFlow = MutableSharedFlow<AccountModel?>()
    private val accountRepository = mock<AccountRepository> {
        every { getActiveAsFlow() } returns accountFlow
    }
    private val feedTypeFlow = MutableSharedFlow<List<FeedTypeModel>>()
    private val feedTypeRepository = mock<FeedTypeRepository> {
        every { getAllAsFlow(any()) } returns feedTypeFlow
    }
    private val circlesRepository = mock<CirclesRepository> {
        everySuspend { getAll() } returns null
    }

    private val sut = DefaultGetTimelineTypesUseCase(
        accountRepository = accountRepository,
        feedTypeRepository = feedTypeRepository,
        circlesRepository = circlesRepository,
        dispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `given no active account when invoked it returns nothing`() = runTest {
        accountFlow.emit(null)

        sut.invoke().test {
            val types = awaitItem()
            assertTrue(types.isEmpty())
        }
    }

    @Test
    fun `given active anonymous account when invoked it returns the default types`() = runTest {
        accountFlow.emit(AccountModel())
        feedTypeFlow.emit(
            listOf(
                FeedTypeModel(id = 1, type = TimelineType.All),
                FeedTypeModel(id = 0, type = TimelineType.Local),
            ),
        )
        val expected = listOf(
            TimelineType.All,
            TimelineType.Local,
        )

        sut.invoke().test {
            val types = awaitItem()
            assertEquals(expected, types)
        }
    }

    @Test
    fun `given active logged account when invoked it returns the default types`() = runTest {
        accountFlow.emit(AccountModel())
        feedTypeFlow.emit(
            listOf(
                FeedTypeModel(id = 1, type = TimelineType.All),
                FeedTypeModel(id = 0, type = TimelineType.Local),
            ),
        )
        accountFlow.emit(AccountModel(id = 1, handle = "example.com"))
        feedTypeFlow.emit(
            listOf(
                FeedTypeModel(id = 1, type = TimelineType.All),
                FeedTypeModel(id = 2, type = TimelineType.Subscriptions),
                FeedTypeModel(id = 0, type = TimelineType.Local),
            ),
        )
        val expected = listOf(
            TimelineType.All,
            TimelineType.Subscriptions,
            TimelineType.Local,
        )

        sut.invoke().test {
            val types = awaitItem()
            assertEquals(expected, types)
        }
    }

    @Test
    fun `given circles account when invoked it returns the default types`() = runTest {
        accountFlow.emit(AccountModel())
        feedTypeFlow.emit(
            listOf(
                FeedTypeModel(id = 1, type = TimelineType.All),
                FeedTypeModel(id = 0, type = TimelineType.Local),
            ),
        )
        accountFlow.emit(AccountModel(id = 1, handle = "example.com"))
        val circle = CircleModel(name = "test", id = "0")
        everySuspend { circlesRepository.getAll() } returns listOf(circle)
        feedTypeFlow.emit(
            listOf(
                FeedTypeModel(id = 1, type = TimelineType.All),
                FeedTypeModel(id = 2, type = TimelineType.Subscriptions),
                FeedTypeModel(id = 0, type = TimelineType.Local),
                FeedTypeModel(id = 0, type = TimelineType.Circle(), info = circle.id),
            ),
        )
        val expected = listOf(
            TimelineType.All,
            TimelineType.Subscriptions,
            TimelineType.Local,
            TimelineType.Circle(circle),
        )

        sut.invoke().test {
            val types = awaitItem()
            assertEquals(expected, types)
        }
    }

    @Test
    fun `when refresh then result is as expected`() = runTest {
        accountFlow.emit(AccountModel(id = 1, handle = "example.com"))
        val circle1 = CircleModel(name = "test 1", id = "0")
        val circle2 = CircleModel(name = "test 2", id = "0")
        everySuspend { circlesRepository.getAll() } returns listOf(circle1)
        feedTypeFlow.emit(
            listOf(
                FeedTypeModel(id = 1, type = TimelineType.All),
                FeedTypeModel(id = 0, type = TimelineType.Circle(), info = circle1.id),
            ),
        )

        sut.invoke().test {
            val initialTypes = awaitItem()
            assertEquals(
                listOf(
                    TimelineType.All,
                    TimelineType.Circle(circle1),
                ),
                initialTypes,
            )

            everySuspend { circlesRepository.getAll() } returns listOf(circle2)
            sut.refresh()

            val updatedTypes = awaitItem()
            assertEquals(
                listOf(
                    TimelineType.All,
                    TimelineType.Circle(circle2),
                ),
                updatedTypes,
            )
        }

        verifySuspend(VerifyMode.exactly(2)) {
            circlesRepository.getAll()
        }
    }
}
