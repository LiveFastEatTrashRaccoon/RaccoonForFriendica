package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.ReportService
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReportCategory
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DefaultReportRepositoryTest {
    private val reportService = mock<ReportService>()
    private val provider = mock<ServiceProvider> {
        every { report } returns reportService
    }
    private val sut = DefaultReportRepository(provider)

    @Test
    fun `when create then result and interactions are as expected`() = runTest {
        everySuspend { reportService.create(any()) } returns true

        val res = sut.create(
            userId = "userId",
            entryIds = listOf("1", "2"),
            comment = "comment",
            forward = true,
            category = ReportCategory.Other,
            ruleIds = listOf("r1"),
        )

        assertTrue(res)
        verifySuspend { reportService.create(any()) }
    }
}
