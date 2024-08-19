package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.OAuthToken
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.toOauthToken
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.ExchangeTokenForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.toJson
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ClientApplicationModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultCredentialsRepository(
    private val provider: ServiceProvider,
    engine: HttpClientEngine = provideHttpClientEngine(),
) : CredentialsRepository {
    private val httpClient = HttpClient(engine)

    override suspend fun validate(
        node: String,
        credentials: ApiCredentials,
    ): UserModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.changeNode(node)
                provider.setAuth(credentials.toServiceCredentials())
                provider.users.verifyCredentials().toModel()
            }.getOrNull()
        }

    override suspend fun validateNode(node: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.changeNode(node)
                provider.timeline.getPublic()
                true
            }.getOrElse { false }
        }

    override suspend fun createApplication(
        node: String,
        clientName: String,
        website: String,
        redirectUri: String,
        scopes: String,
    ): ClientApplicationModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.changeNode(node)
                val data =
                    CreateAppForm(
                        clientName = clientName,
                        website = website,
                        redirectUris = redirectUri,
                        scopes = scopes,
                    )
                provider.apps.create(data).toModel()
            }.getOrElse { null }
        }

    override suspend fun exchangeToken(
        node: String,
        path: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        grantType: String,
        code: String,
    ): ApiCredentials? =
        withContext(Dispatchers.IO) {
            runCatching {
                val json =
                    ExchangeTokenForm(
                        clientId = clientId,
                        clientSecret = clientSecret,
                        redirectUri = redirectUri,
                        grantType = grantType,
                        code = code,
                    ).toJson()

                val url =
                    URLBuilder()
                        .apply {
                            protocol = URLProtocol.HTTPS
                            host = node
                            path(path)
                        }.toString()
                val responseBody =
                    httpClient
                        .preparePost(url) {
                            setBody(json)
                        }.execute()
                        .bodyAsText()
                responseBody.toOauthToken().toModel()
            }.getOrNull()
        }
}

private fun Application.toModel() =
    ClientApplicationModel(
        name = name,
        webSite = webSite,
        clientId = clientId,
        clientSecret = clientSecret,
    )

private fun CredentialAccount.toModel() =
    UserModel(
        bot = bot,
        created = createdAt,
        displayName = displayName,
        group = group,
        handle = acct,
        id = id,
        username = username,
    )

private fun OAuthToken.toModel(): ApiCredentials =
    ApiCredentials.OAuth2(
        accessToken = accessToken,
        refreshToken = refreshToken.orEmpty(),
    )
