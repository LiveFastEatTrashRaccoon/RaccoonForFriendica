package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.Acknowledgement
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.models.AcknowledgementModel

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
