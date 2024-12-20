package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.Acknowledgement
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.models.AcknowledgementModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultAcknowledgementsRepository(
    private val dataSource: AcknowledgementsRemoteDataSource,
) : AcknowledgementsRepository {
    override suspend fun getAll(): List<AcknowledgementModel>? =
        withContext(Dispatchers.IO) {
            dataSource.getAcknowledgements()?.map { it.toModel() }
        }
}

private fun Acknowledgement.toModel() =
    AcknowledgementModel(
        title = title,
        url = url,
        avatar = avatar,
        subtitle = subtitle,
    )
