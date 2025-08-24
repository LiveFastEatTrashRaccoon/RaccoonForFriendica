package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Event
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.EventService
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultEventRepositoryTest {
    private val eventService = mock<EventService>()
    private val serviceProvider =
        mock<ServiceProvider> { every { this@mock.event } returns eventService }
    private val sut =
        DefaultEventRepository(
            provider = serviceProvider,
        )

    @Test
    fun `when getAll then result is as expected`() = runTest {
        val list =
            listOf(
                Event(
                    id = 1,
                    uri = "",
                    name = "",
                    description = "",
                    startTime = "",
                ),
            )
        everySuspend { eventService.getAll(any(), any()) } returns list

        val res = sut.getAll(pageCursor = "0")

        assertEquals(list.map { it.toModel() }, res)
        verifySuspend {
            eventService.getAll(
                maxId = 0,
                count = 20,
            )
        }
    }
}
