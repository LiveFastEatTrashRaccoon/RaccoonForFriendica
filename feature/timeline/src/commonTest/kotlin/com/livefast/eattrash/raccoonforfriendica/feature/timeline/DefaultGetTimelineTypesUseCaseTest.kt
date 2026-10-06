package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultGetTimelineTypesUseCaseTest {
    private val circlesRepository = mock<CirclesRepository>()
    private val identityRepository = mock<IdentityRepository>()

    private val sut = DefaultGetTimelineTypesUseCase(
        circlesRepository = circlesRepository,
        identityRepository = identityRepository,
    )

    @Test
    fun `given not logged when it is invoked it returns the data`() = runTest {
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        everySuspend { circlesRepository.getAll() } returns emptyList()

        sut.invoke().test {
            val types = awaitItem()
            assertTrue(types.contains(TimelineType.Local))
            assertTrue(types.contains(TimelineType.All))
            assertFalse(types.contains(TimelineType.Subscriptions))
        }
    }

    @Test
    fun `given logged when it is invoked it returns the data`() = runTest {
        every { identityRepository.currentUser } returns MutableStateFlow(UserModel(id = "1"))
        everySuspend { circlesRepository.getAll() } returns emptyList()

        sut.invoke().test {
            val types = awaitItem()
            assertTrue(types.contains(TimelineType.Local))
            assertTrue(types.contains(TimelineType.All))
            assertTrue(types.contains(TimelineType.Subscriptions))
        }
    }

    @Test
    fun `given circles when it is invoked it returns the data`() = runTest {
        every { identityRepository.currentUser } returns MutableStateFlow(UserModel(id = "1"))
        val circles = listOf(
            CircleModel(id = "1", name = "Circle 1"),
            CircleModel(id = "2", name = "Circle 2"),
        )
        everySuspend { circlesRepository.getAll() } returns circles

        sut.invoke().test {
            val types = awaitItem()
            assertTrue(types.contains(TimelineType.Local))
            assertTrue(types.contains(TimelineType.All))
            assertTrue(types.contains(TimelineType.Subscriptions))
            assertEquals(2, types.count { it is TimelineType.Circle })
            assertTrue(types.contains(TimelineType.Circle(circles[0])))
            assertTrue(types.contains(TimelineType.Circle(circles[1])))
        }
    }

    @Test
    fun `given null circles from repository when invoked then returns defaults without crashing`() = runTest {
        every { identityRepository.currentUser } returns MutableStateFlow(UserModel(id = "1"))
        everySuspend { circlesRepository.getAll() } returns null

        sut.invoke().test {
            val types = awaitItem()
            assertTrue(types.contains(TimelineType.Local))
            assertTrue(types.contains(TimelineType.All))
            assertTrue(types.contains(TimelineType.Subscriptions))
            assertEquals(0, types.count { it is TimelineType.Circle })
        }
    }

    @Test
    fun `when user identity changes then flow emits updated list`() = runTest {
        val userFlow = MutableStateFlow<UserModel?>(null)
        every { identityRepository.currentUser } returns userFlow
        everySuspend { circlesRepository.getAll() } returns emptyList()

        sut.invoke().test {
            val initialTypes = awaitItem()
            assertFalse(initialTypes.contains(TimelineType.Subscriptions))

            userFlow.value = UserModel(id = "1")

            val updatedTypes = awaitItem()
            assertTrue(updatedTypes.contains(TimelineType.Subscriptions))
        }
    }

    @Test
    fun `when refresh is called then flow emits updated list with new circles`() = runTest {
        every { identityRepository.currentUser } returns MutableStateFlow(UserModel(id = "1"))

        var circleList = emptyList<CircleModel>()
        everySuspend { circlesRepository.getAll() } calls { circleList }

        sut.invoke().test {
            val initialTypes = awaitItem()
            assertEquals(0, initialTypes.count { it is TimelineType.Circle })

            val circle = CircleModel(id = "1", name = "New Circle")
            circleList = listOf(circle)
            sut.refresh()

            val updatedTypes = awaitItem()
            assertEquals(1, updatedTypes.count { it is TimelineType.Circle })
            assertTrue(updatedTypes.contains(TimelineType.Circle(circle)))
        }
    }
}
