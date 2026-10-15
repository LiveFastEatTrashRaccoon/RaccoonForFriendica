package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.navigation3.runtime.NavBackStack
import app.cash.turbine.test
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultNavigationCoordinatorTest {
    private val navigationAdapterFactory = mock<NavigationAdapterFactory>()
    private val bottomNavigationAdapterFactory = mock<BottomNavigationAdapterFactory>()

    private val sut =
        DefaultNavigationCoordinator(
            navigationAdapterFactory = navigationAdapterFactory,
            bottomNavigationAdapterFactory = bottomNavigationAdapterFactory,
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
        val adapter =
            mock<BottomNavigationAdapter>(MockMode.autoUnit) {
                every { currentSection } returns MutableStateFlow(BottomNavigationSection.Home)
            }
        every { bottomNavigationAdapterFactory.create(any()) } returns adapter
        sut.setupBottomNavigationAdapter(NavBackStack())
        val section = BottomNavigationSection.Explore

        sut.setCurrentSection(section)

        verify { adapter.navigate(section) }
    }

    @Test
    fun `when setCurrentSection twice then onDoubleTabSelection is triggered`() = runTest {
        val section = BottomNavigationSection.Explore
        val adapter =
            mock<BottomNavigationAdapter>(MockMode.autoUnit) {
                every { currentSection } returns MutableStateFlow(section)
            }
        every { bottomNavigationAdapterFactory.create(any()) } returns adapter
        sut.setupBottomNavigationAdapter(NavBackStack())

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
    fun `given adapter can pop when setupRootNavigationAdapter then canPop is as expected`() = runTest {
        val initial = sut.canPop.value
        assertFalse(initial)
        val adapter =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns MutableStateFlow(true)
            }
        every { navigationAdapterFactory.create(any()) } returns adapter
        sut.setupRootNavigationAdapter(NavBackStack())

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
        val adapter =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns MutableStateFlow(true)
            }
        every { navigationAdapterFactory.create(any()) } returns adapter
        sut.setupRootNavigationAdapter(NavBackStack())

        launch {
            sut.push(destination)
        }
        advanceTimeBy(DELAY)

        val canPop = sut.canPop.value
        assertTrue(canPop)
        verify {
            adapter.navigate(destination)
        }
    }

    @Test
    fun `when replace then interactions are as expected`() = runTest {
        val destination = Destination.Settings
        val adapter =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns MutableStateFlow(true)
            }
        every { navigationAdapterFactory.create(any()) } returns adapter
        sut.setupRootNavigationAdapter(NavBackStack())

        launch {
            sut.replace(destination)
        }
        advanceTimeBy(DELAY)

        val canPop = sut.canPop.value
        assertTrue(canPop)
        verify {
            adapter.navigate(destination = destination, replaceTop = true)
        }
    }

    @Test
    fun `when pop then interactions are as expected`() = runTest {
        val adapter =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns MutableStateFlow(false)
            }
        every { navigationAdapterFactory.create(any()) } returns adapter
        sut.setupRootNavigationAdapter(NavBackStack())

        launch {
            sut.pop()
        }
        advanceTimeBy(DELAY)

        val canPop = sut.canPop.value
        assertFalse(canPop)
        verify {
            adapter.pop()
        }
    }

    @Test
    fun `when popUntilRoot then interactions are as expected`() = runTest {
        val adapter =
            mock<NavigationAdapter>(MockMode.autoUnit) {
                every { canPop } returns MutableStateFlow(false)
            }
        every { navigationAdapterFactory.create(any()) } returns adapter
        sut.setupRootNavigationAdapter(NavBackStack())

        sut.popUntilRoot()

        verify {
            adapter.popUntilRoot()
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
        val DELAY = 250.milliseconds
    }
}
