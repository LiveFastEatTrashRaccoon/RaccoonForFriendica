package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Instance
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.InstanceRule
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.service.InstanceService
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultNodeInfoRepositoryTest {
    private val instanceService = mock<InstanceService>()
    private val serviceProvider =
        mock<ServiceProvider> { every { instance } returns instanceService }
    private val sut = DefaultNodeInfoRepository(
        provider = serviceProvider,
        json = Json,
    )

    @Test
    fun `when getInfo then result is as expected`() = runTest {
        val instance = Instance(domain = "example.com")
        everySuspend { instanceService.getInfo() } returns instance

        val res = sut.getInfo()

        assertEquals(instance.toModel(), res)
    }

    @Test
    fun `when getRules then result is as expected`() = runTest {
        val list =
            listOf(
                InstanceRule(id = "1", text = "rule 1"),
                InstanceRule(id = "2", text = "rule 2"),
            )
        everySuspend { instanceService.getRules() } returns list

        val res = sut.getRules()

        assertEquals(list.map { it.toModel() }, res)
    }
}
