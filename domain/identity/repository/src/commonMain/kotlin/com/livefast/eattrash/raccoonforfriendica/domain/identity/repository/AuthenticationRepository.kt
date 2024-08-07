package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

interface AuthenticationRepository {
    suspend fun validateCredentials(
        node: String,
        user: String,
        pass: String,
    ): Boolean
}
