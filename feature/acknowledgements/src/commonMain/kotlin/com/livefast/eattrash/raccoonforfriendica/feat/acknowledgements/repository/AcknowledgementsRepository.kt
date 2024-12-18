package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository

import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.models.AcknowledgementModel

internal interface AcknowledgementsRepository {
    suspend fun getAll(): List<AcknowledgementModel>?
}
