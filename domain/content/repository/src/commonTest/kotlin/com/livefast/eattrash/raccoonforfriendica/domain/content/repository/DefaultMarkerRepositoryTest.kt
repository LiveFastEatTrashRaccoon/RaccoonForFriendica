package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.MarkerItem
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Markers
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.MarkerService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultMarkerRepositoryTest {
    private val markerService = mock<MarkerService>()
    private val serviceProvider = mock<ServiceProvider> { every { marker } returns markerService }
    private val sut =
        DefaultMarkerRepository(
            provider = serviceProvider,
        )

    @Test
    fun `when get then result is as expected`() = runTest {
        everySuspend { markerService.get(any()) } returns Markers(notifications = MarkerItem("1"))

        val res = sut.get(type = MarkerType.Notifications)

        assertEquals(expected = MarkerModel(type = MarkerType.Notifications, "1"), actual = res)
        verifySuspend {
            markerService.get(listOf("notifications"))
        }
    }

    @Test
    fun `when get twice then result is as expected`() = runTest {
        everySuspend { markerService.get(any()) } returns Markers(notifications = MarkerItem("1"))

        sut.get(type = MarkerType.Notifications)
        val res = sut.get(type = MarkerType.Notifications)

        assertEquals(expected = MarkerModel(type = MarkerType.Notifications, "1"), actual = res)
        verifySuspend(VerifyMode.exactly(1)) {
            markerService.get(listOf("notifications"))
        }
    }

    @Test
    fun `when get twice refreshing then result is as expected`() = runTest {
        everySuspend { markerService.get(any()) } returns Markers(notifications = MarkerItem("1"))

        sut.get(type = MarkerType.Notifications)
        val res = sut.get(type = MarkerType.Notifications, refresh = true)

        assertEquals(expected = MarkerModel(type = MarkerType.Notifications, "1"), actual = res)
        verifySuspend(VerifyMode.exactly(2)) {
            markerService.get(listOf("notifications"))
        }
    }

    @Test
    fun `when update then result is as expected`() = runTest {
        everySuspend { markerService.update(any()) } returns
            Markers(
                notifications =
                MarkerItem(
                    "2",
                ),
            )

        val res = sut.update(MarkerType.Notifications, id = "2")

        assertEquals(expected = MarkerModel(type = MarkerType.Notifications, "2"), actual = res)
        verifySuspend {
            markerService.update(
                matching {
                    it.formData["notifications[last_read_id]"] == "2"
                },
            )
        }
    }
}
