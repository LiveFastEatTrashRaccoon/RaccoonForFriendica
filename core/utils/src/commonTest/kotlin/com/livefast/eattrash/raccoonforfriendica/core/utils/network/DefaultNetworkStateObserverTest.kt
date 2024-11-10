package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import app.cash.turbine.test
import dev.jordond.connectivity.Connectivity
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultNetworkStateObserverTest {
    private val connectivityStatusUpdates = MutableSharedFlow<Connectivity.Status>()
    private val connectivity =
        mock<Connectivity>(MockMode.autoUnit) {
            every { statusUpdates } returns connectivityStatusUpdates
        }

    private val sut =
        DefaultNetworkStateObserver(
            connectivity = connectivity,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given not started when connectivity changes then result is as expected`() =
        runTest {
            sut.state.test {
                val state1 = awaitItem()
                assertEquals(NetworkState.Disconnected, state1)
                connectivityStatusUpdates.emit(Connectivity.Status.Connected(false))
                val state2 = awaitItem()
                assertEquals(NetworkState.Connected(false), state2)
            }

            verify(VerifyMode.not) {
                connectivity.start()
                connectivity.stop()
            }
        }

    @Test
    fun `given started when connectivity changes then result is as expected`() =
        runTest {
            sut.state.test {
                val state1 = awaitItem()
                assertEquals(NetworkState.Disconnected, state1)

                sut.start()
                connectivityStatusUpdates.emit(Connectivity.Status.Connected(false))

                val state2 = awaitItem()
                assertEquals(NetworkState.Connected(false), state2)
            }

            verify {
                connectivity.start()
            }
        }

    @Test
    fun `given started and metered network when connectivity changes then result is as expected`() =
        runTest {
            sut.state.test {
                val state1 = awaitItem()
                assertEquals(NetworkState.Disconnected, state1)

                sut.start()
                connectivityStatusUpdates.emit(Connectivity.Status.Connected(true))

                val state2 = awaitItem()
                assertEquals(NetworkState.Connected(true), state2)
            }

            verify {
                connectivity.start()
            }
        }
}
