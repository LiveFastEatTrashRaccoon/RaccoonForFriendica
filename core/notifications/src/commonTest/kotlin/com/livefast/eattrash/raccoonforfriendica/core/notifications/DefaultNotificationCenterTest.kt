package com.livefast.eattrash.raccoonforfriendica.core.notifications

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TimelineEntryUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultNotificationCenterTest {
    private val sut =
        DefaultNotificationCenter(
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
    fun `given subscription when send event then event is received just once`() =
        runTest {
            val expected =
                TimelineEntryUpdatedEvent(
                    entry = TimelineEntryModel(id = "0", content = ""),
                )
            launch {
                sut.send(expected)
            }

            sut.subscribe(TimelineEntryUpdatedEvent::class).test {
                val actual = awaitItem()
                assertEquals(expected, actual)
            }

            sut.subscribe(TimelineEntryUpdatedEvent::class).test {
                expectNoEvents()
            }
        }
}
