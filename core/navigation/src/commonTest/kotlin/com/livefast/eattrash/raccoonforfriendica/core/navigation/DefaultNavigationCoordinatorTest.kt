package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultNavigationCoordinatorTest {
    private val sut =
        DefaultNavigationCoordinator(
            dispatcher = UnconfinedTestDispatcher(),
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when setBottomBarScrollConnection then result is as expected`() = runTest {
        val connection = mock<NestedScrollConnection>(MockMode.autofill)
        sut.setBottomBarScrollConnection(connection)

        val res = sut.getBottomBarScrollConnection()
        assertEquals(connection, res)
    }

    @Test
    fun `when setCurrentSection then result is as expected`() {
        val section = BottomNavigationSection.Explore

        sut.setCurrentSection(section)

        val res = sut.currentBottomNavSection.value
        assertEquals(section, res)
    }

    @Test
    fun `when setCurrentSection twice then onDoubleTabSelection is triggered`() = runTest {
        val section = BottomNavigationSection.Explore
        sut.setCurrentSection(section)
        launch {
            delay(DELAY)
            sut.setCurrentSection(section)
        }
        sut.onDoubleTabSelection.test {
            val res = awaitItem()
            assertEquals(section, res)
        }
    }

    @Test
    fun `given navigator can pop when root navigator set then canPop is as expected`() = runTest {
        val initial = sut.canPop.value
        assertFalse(initial)
        val navigator =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns true
            }

        sut.setRootNavigator(navigator)

        val value = sut.canPop.value
        assertTrue(value)
    }

    @Test
    fun `when setExitMessageVisible then value is as expected`() = runTest {
        val initial = sut.exitMessageVisible.value
        assertFalse(initial)

        sut.setExitMessageVisible(true)
        val value = sut.exitMessageVisible.value
        assertTrue(value)
    }

    @Test
    fun `when showGlobalMessage then value is as expected`() = runTest {
        val text = "Global message"

        launch {
            sut.showGlobalMessage(text)
        }

        sut.globalMessage.test {
            val item = awaitItem()
            assertEquals(text, item)
        }
    }

    @Test
    fun `when push then interactions are as expected`() = runTest {
        val destination = Destination.Settings
        val navigator =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns true
            }
        sut.setRootNavigator(navigator)

        launch {
            sut.push(destination)
        }
        advanceTimeBy(DELAY)

        val canPop = sut.canPop.value
        assertTrue(canPop)
        verify {
            navigator.navigate(destination)
        }
    }

    @Test
    fun `when replace then interactions are as expected`() = runTest {
        val destination = Destination.Settings
        val navigator =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns true
            }
        sut.setRootNavigator(navigator)

        launch {
            sut.replace(destination)
        }
        advanceTimeBy(DELAY)

        val canPop = sut.canPop.value
        assertTrue(canPop)
        verify {
            navigator.navigate(destination = destination, replaceTop = true)
        }
    }

    @Test
    fun `when pop then interactions are as expected`() = runTest {
        val navigator =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns false
            }
        sut.setRootNavigator(navigator)

        launch {
            sut.pop()
        }
        advanceTimeBy(DELAY)

        val canPop = sut.canPop.value
        assertFalse(canPop)
        verify {
            navigator.pop()
        }
    }

    @Test
    fun `when popUntilRoot then interactions are as expected`() = runTest {
        val navigator =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns false
            }
        sut.setRootNavigator(navigator)

        sut.popUntilRoot()

        verify {
            navigator.popUntilRoot()
        }
    }

    @Test
    fun `when submitDeeplink then event flow emits`() = runTest {
        val url = "https://www.google.com"
        launch {
            advanceTimeBy(DELAY)
            sut.submitDeeplink(url)
        }
        sut.deepLinkUrl.test {
            val item = awaitItem()
            assertEquals(url, item)
        }
    }

    companion object {
        const val DELAY = 250L
    }
}
