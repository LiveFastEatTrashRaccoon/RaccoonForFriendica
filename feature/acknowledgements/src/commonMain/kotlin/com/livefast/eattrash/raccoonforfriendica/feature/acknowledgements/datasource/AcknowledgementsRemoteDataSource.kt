package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource

internal interface AcknowledgementsRemoteDataSource {
    suspend fun getAcknowledgements(): List<Acknowledgement>?
}
