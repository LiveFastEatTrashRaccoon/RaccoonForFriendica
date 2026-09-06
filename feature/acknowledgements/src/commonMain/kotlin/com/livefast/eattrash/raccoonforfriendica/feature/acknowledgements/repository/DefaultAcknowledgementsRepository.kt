package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource.Acknowledgement
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.models.AcknowledgementModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultAcknowledgementsRepository(private val dataSource: AcknowledgementsRemoteDataSource) :
    AcknowledgementsRepository {
    override suspend fun getAll(): List<AcknowledgementModel>? = dataSource.getAcknowledgements()?.map { it.toModel() }
}

private fun Acknowledgement.toModel() = AcknowledgementModel(
    title = title,
    url = url,
    avatar = avatar,
    subtitle = subtitle,
)
