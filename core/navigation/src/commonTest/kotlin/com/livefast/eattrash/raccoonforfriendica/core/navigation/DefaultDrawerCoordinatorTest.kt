package com.livefast.eattrash.raccoonforfriendica.core.navigation

import app.cash.turbine.test
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultDrawerCoordinatorTest {
    private val sut = DefaultDrawerCoordinator()

    @Test
    fun `when toggleDrawer then event is emitted`() = runTest {
        launch {
            sut.toggleDrawer()
        }

        sut.events.test {
            val evt = awaitItem()
            assertEquals(DrawerEvent.Toggle, evt)
        }
    }

    @Test
    fun `when closeDrawer then event is emitted`() = runTest {
        launch {
            sut.closeDrawer()
        }

        sut.events.test {
            val evt = awaitItem()
            assertEquals(DrawerEvent.Close, evt)
        }
    }

    @Test
    fun `when setGesturesEnabled then state is updated`() = runTest {
        val initial = sut.gesturesEnabled.value
        assertTrue(initial)

        sut.setGesturesEnabled(false)

        val value = sut.gesturesEnabled.value
        assertFalse(value)
    }

    @Test
    fun `when sendEvent then event is emitted`() = runTest {
        launch {
            sut.sendEvent(DrawerEvent.Toggle)
        }
        sut.events.test {
            val evt = awaitItem()
            assertEquals(DrawerEvent.Toggle, evt)
        }
    }
}
