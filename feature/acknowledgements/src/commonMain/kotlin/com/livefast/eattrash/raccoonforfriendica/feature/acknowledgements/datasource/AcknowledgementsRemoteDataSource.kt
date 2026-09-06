package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.datasource

interface AcknowledgementsRemoteDataSource {
    suspend fun getAcknowledgements(): List<Acknowledgement>?
}
