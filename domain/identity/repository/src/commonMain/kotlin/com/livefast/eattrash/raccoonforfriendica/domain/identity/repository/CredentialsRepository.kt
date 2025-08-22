package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ClientApplicationModel

interface CredentialsRepository {
    suspend fun validate(node: String, credentials: ApiCredentials): UserModel?

    suspend fun validateNode(node: String): Boolean

    suspend fun createApplication(
        node: String,
        clientName: String,
        website: String,
        redirectUri: String,
        scopes: String,
    ): ClientApplicationModel?

    suspend fun validateApplicationCredentials(node: String, credentials: ApiCredentials): Boolean

    suspend fun exchangeToken(
        node: String,
        path: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        grantType: String,
        code: String,
    ): ApiCredentials?

    suspend fun issueNewToken(
        node: String,
        path: String,
        clientId: String,
        clientSecret: String,
        grantType: String,
        refreshToken: String,
    ): ApiCredentials?
}
