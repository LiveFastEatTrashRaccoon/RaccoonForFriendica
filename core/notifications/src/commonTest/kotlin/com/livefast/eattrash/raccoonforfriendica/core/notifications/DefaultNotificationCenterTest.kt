package com.livefast.eattrash.raccoonforfriendica.core.notifications

import app.cash.turbine.test
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.AlbumsUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.DraftDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostCreatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostDeletedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.PostUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.TagUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.core.notifications.events.UserUpdatedEvent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
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
    fun `given subscription when send UserUpdatedEvent then event is received just once`() = runTest {
        val expected =
            UserUpdatedEvent(user = UserModel(id = "0"))
        launch {
            sut.send(expected)
        }

        sut.subscribe(UserUpdatedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(UserUpdatedEvent::class).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given subscription when send PostCreatedEvent then event is received just once`() = runTest {
        val expected = PostCreatedEvent(post = PostModel(id = "0", content = ""))
        launch {
            sut.send(expected)
        }

        sut.subscribe(PostCreatedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(PostCreatedEvent::class).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given subscription when send PostUpdatedEvent then event is received just once`() = runTest {
        val expected = PostUpdatedEvent(post = PostModel(id = "0", content = ""))
        launch {
            sut.send(expected)
        }

        sut.subscribe(PostUpdatedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(PostUpdatedEvent::class).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given subscription when send PostDeletedEvent then event is received just once`() = runTest {
        val expected = PostDeletedEvent(id = "0")
        launch {
            sut.send(expected)
        }

        sut.subscribe(PostDeletedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(PostDeletedEvent::class).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given subscription when send TagUpdatedEvent then event is received just once`() = runTest {
        val expected = TagUpdatedEvent(tag = TagModel(name = "tag"))
        launch {
            sut.send(expected)
        }

        sut.subscribe(TagUpdatedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(TagUpdatedEvent::class).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given subscription when send AlbumsUpdatedEvent then event is received just once`() = runTest {
        val expected = AlbumsUpdatedEvent
        launch {
            sut.send(expected)
        }

        sut.subscribe(AlbumsUpdatedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(AlbumsUpdatedEvent::class).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given subscription when send DraftDeletedEvent then event is received just once`() = runTest {
        val expected = DraftDeletedEvent(id = "0")
        launch {
            sut.send(expected)
        }

        sut.subscribe(DraftDeletedEvent::class).test {
            val actual = awaitItem()
            assertEquals(expected, actual)
        }

        sut.subscribe(DraftDeletedEvent::class).test {
            expectNoEvents()
        }
    }
}
