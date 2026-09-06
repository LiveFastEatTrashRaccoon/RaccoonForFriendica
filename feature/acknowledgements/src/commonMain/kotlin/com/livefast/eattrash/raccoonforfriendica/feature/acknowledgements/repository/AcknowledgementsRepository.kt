package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.models.AcknowledgementModel

interface AcknowledgementsRepository {
    suspend fun getAll(): List<AcknowledgementModel>?
}
