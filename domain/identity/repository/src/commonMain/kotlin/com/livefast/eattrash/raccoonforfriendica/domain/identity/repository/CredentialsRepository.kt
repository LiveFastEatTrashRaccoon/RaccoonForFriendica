package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

interface CredentialsRepository {
    suspend fun validate(
        node: String,
        user: String,
        pass: String,
    ): Boolean
}
