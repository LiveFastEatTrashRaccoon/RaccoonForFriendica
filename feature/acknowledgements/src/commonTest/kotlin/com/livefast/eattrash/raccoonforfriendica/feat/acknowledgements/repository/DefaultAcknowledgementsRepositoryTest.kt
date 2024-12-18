package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.Acknowledgement
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.models.AcknowledgementModel
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultAcknowledgementsRepositoryTest {
    private val dataSource = mock<AcknowledgementsRemoteDataSource>()
    private val sut = DefaultAcknowledgementsRepository(dataSource = dataSource)

    @Test
    fun `given no results when getAll then result and interactions are as expected`() =
        runTest {
            everySuspend { dataSource.getAcknowledgements() } returns emptyList()

            val res = sut.getAll()

            assertEquals(emptyList(), res)
            verifySuspend {
                dataSource.getAcknowledgements()
            }
        }

    @Test
    fun `given results when getAll then result and interactions are as expected`() =
        runTest {
            val name = "Tizio Caio Sempronio"
            val list = listOf(Acknowledgement(title = name))
            everySuspend { dataSource.getAcknowledgements() } returns list

            val res = sut.getAll()

            assertEquals(listOf(AcknowledgementModel(title = name)), res)
            verifySuspend {
                dataSource.getAcknowledgements()
            }
        }
}
