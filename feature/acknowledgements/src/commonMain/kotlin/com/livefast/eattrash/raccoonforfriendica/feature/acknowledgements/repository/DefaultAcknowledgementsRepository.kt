package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource.Acknowledgement
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.models.AcknowledgementModel
import org.koin.core.annotation.Single

@Single
internal class DefaultAcknowledgementsRepository(private val dataSource: AcknowledgementsRemoteDataSource) :
    AcknowledgementsRepository {
    override suspend fun getAll(): List<AcknowledgementModel>? = dataSource.getAcknowledgements()?.map { it.toModel() }
}

private fun Acknowledgement.toModel() = AcknowledgementModel(
    title = title,
    url = url,
    avatar = avatar,
    subtitle = subtitle,
)
