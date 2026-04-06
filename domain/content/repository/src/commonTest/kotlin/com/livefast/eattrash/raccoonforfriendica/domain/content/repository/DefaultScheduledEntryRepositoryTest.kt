package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.ScheduledStatus
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.StatusService
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultScheduledEntryRepositoryTest {
    private val statusService = mock<StatusService>()
    private val provider = mock<ServiceProvider> {
        every { status } returns statusService
    }
    private val sut = DefaultScheduledEntryRepository(provider)

    @Test
    fun `given results when getAll then result and interactions are as expected`() = runTest {
        val scheduledStatuses = listOf(ScheduledStatus(id = "1"))
        everySuspend { statusService.getScheduled(any(), any()) } returns scheduledStatuses

        val res = sut.getAll(null)

        assertNotNull(res)
        assertEquals(1, res.size)
        assertEquals("1", res.first().id)
        verifySuspend { statusService.getScheduled(maxId = null, limit = 20) }
    }

    @Test
    fun `given error when getAll then return null`() = runTest {
        everySuspend { statusService.getScheduled(any(), any()) } throws IOException()

        val res = sut.getAll(null)

        assertNull(res)
    }

    @Test
    fun `given results when getById then result and interactions are as expected`() = runTest {
        val scheduledStatus = ScheduledStatus(id = "1")
        everySuspend { statusService.getScheduledById(any()) } returns scheduledStatus

        val res = sut.getById("1")

        assertNotNull(res)
        assertEquals("1", res.id)
        verifySuspend { statusService.getScheduledById("1") }
    }

    @Test
    fun `given result when update then result and interactions are as expected`() = runTest {
        val scheduledStatus = ScheduledStatus(id = "1")
        everySuspend { statusService.updateScheduled(any(), any()) } returns scheduledStatus

        val res = sut.update("1", "2023-01-01T00:00:00Z")

        assertNotNull(res)
        assertEquals("1", res.id)
        verifySuspend { statusService.updateScheduled(id = "1", data = any()) }
    }

    @Test
    fun `when delete then result is as expected`() = runTest {
        everySuspend { statusService.deleteScheduled(any()) } returns true

        val res = sut.delete("1")

        assertTrue(res)
        verifySuspend { statusService.deleteScheduled("1") }
    }
}
