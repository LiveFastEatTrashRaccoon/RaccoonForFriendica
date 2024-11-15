package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import app.cash.turbine.test
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
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
    fun `when setCanGoBackCallback then result is as expected`() =
        runTest {
            val callback = { true }
            sut.setCanGoBackCallback(callback)

            val res = sut.getCanGoBackCallback()
            assertEquals(callback, res)
        }

    @Test
    fun `when setBottomBarScrollConnection then result is as expected`() =
        runTest {
            val connection = mock<NestedScrollConnection>(MockMode.autofill)
            sut.setBottomBarScrollConnection(connection)

            val res = sut.getBottomBarScrollConnection()
            assertEquals(connection, res)
        }

    @Test
    fun `when setCurrentSection then result is as expected`() {
        val section = BottomNavigationSection.Explore

        sut.setCurrentSection(section)

        val res = sut.currentSection.value
        assertEquals(section, res)
    }

    @Test
    fun `when setCurrentSection twice then onDoubleTabSelection is triggered`() =
        runTest {
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
    fun `given navigator can pop when root navigator set then canPop is as expected`() =
        runTest {
            val initial = sut.canPop.value
            assertFalse(initial)
            val navigator =
                mock<NavigatorAdapter>(MockMode.autoUnit) {
                    every { canPop } returns true
                }

            sut.setRootNavigator(navigator)

            val value = sut.canPop.value
            assertTrue(value)
        }

    @Test
    fun `when setExitMessageVisible then value is as expected`() =
        runTest {
            val initial = sut.exitMessageVisible.value
            assertFalse(initial)

            sut.setExitMessageVisible(true)
            val value = sut.exitMessageVisible.value
            assertTrue(value)
        }

    @Test
    fun `when push then interactions are as expected`() =
        runTest {
            val screen =
                object : Screen {
                    override val key: ScreenKey = "new"

                    @Composable
                    override fun Content() {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            val navigator =
                mock<NavigatorAdapter>(MockMode.autoUnit) {
                    every { canPop } returns true
                }
            sut.setRootNavigator(navigator)

            launch {
                sut.push(screen)
            }
            advanceTimeBy(DELAY)

            val canPop = sut.canPop.value
            assertTrue(canPop)
            verify {
                navigator.push(screen)
            }
        }

    @Test
    fun `when replace then interactions are as expected`() =
        runTest {
            val screen =
                object : Screen {
                    override val key: ScreenKey = "new"

                    @Composable
                    override fun Content() {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            val navigator =
                mock<NavigatorAdapter>(MockMode.autoUnit) {
                    every { canPop } returns true
                }
            sut.setRootNavigator(navigator)

            launch {
                sut.replace(screen)
            }
            advanceTimeBy(DELAY)

            val canPop = sut.canPop.value
            assertTrue(canPop)
            verify {
                navigator.replace(screen)
            }
        }

    @Test
    fun `when pop then interactions are as expected`() =
        runTest {
            val navigator =
                mock<NavigatorAdapter>(MockMode.autoUnit) {
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
    fun `when popUntilRoot then interactions are as expected`() =
        runTest {
            val navigator =
                mock<NavigatorAdapter>(MockMode.autoUnit) {
                    every { canPop } returns false
                }
            sut.setRootNavigator(navigator)

            sut.popUntilRoot()

            verify {
                navigator.popUntilRoot()
            }
        }

    @Test
    fun `when submitDeeplink then event flow emits`() =
        runTest {
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
