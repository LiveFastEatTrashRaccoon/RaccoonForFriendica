package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource

internal interface AcknowledgementsRemoteDataSource {
    suspend fun getAcknowledgements(): List<Acknowledgement>?
}
