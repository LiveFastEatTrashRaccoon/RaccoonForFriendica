package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkState
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.NetworkStateObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ImageLoadingMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultImageAutoloadObserverTest {
    private val networkState = MutableStateFlow<NetworkState>(NetworkState.Disconnected)
    private val settings = MutableStateFlow(SettingsModel(autoloadImages = ImageLoadingMode.Always))
    private val networkStateObserver =
        mock<NetworkStateObserver> {
            every { state } returns networkState
        }
    private val settingsRepository =
        mock<SettingsRepository> {
            every { current } returns settings
        }
    private val sut =
        DefaultImageAutoloadObserver(
            networkStateObserver = networkStateObserver,
            settingsRepository = settingsRepository,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given always mode when initialized then result is as expected`() =
        runTest {
            sut.enabled.test {
                val value = awaitItem()
                assertTrue(value)
            }
        }

    @Test
    fun `given on demand mode when initialized then result is as expected`() =
        runTest {
            sut.enabled.drop(1).test {
                settings.emit(SettingsModel(autoloadImages = ImageLoadingMode.OnDemand))
                val value = awaitItem()
                assertFalse(value)
            }
        }

    @Test
    fun `given on WiFi mode and disconnected when initialized then result is as expected`() =
        runTest {
            sut.enabled.drop(1).test {
                settings.emit(SettingsModel(autoloadImages = ImageLoadingMode.OnWifi))
                val value = awaitItem()
                assertFalse(value)
            }
        }

    @Test
    fun `given on WiFi mode and connected on metered network when initialized then result is as expected`() =
        runTest {
            sut.enabled.drop(1).test {
                settings.emit(SettingsModel(autoloadImages = ImageLoadingMode.OnWifi))
                networkState.emit(NetworkState.Connected(true))
                val value = awaitItem()
                assertFalse(value)
            }
        }

    @Test
    fun `given on WiFi mode and connected when initialized then result is as expected`() =
        runTest {
            sut.enabled.drop(1).test {
                settings.emit(SettingsModel(autoloadImages = ImageLoadingMode.OnWifi))
                awaitItem()
                networkState.emit(NetworkState.Connected(false))
                val value = awaitItem()
                assertTrue(value)
            }
        }
}
