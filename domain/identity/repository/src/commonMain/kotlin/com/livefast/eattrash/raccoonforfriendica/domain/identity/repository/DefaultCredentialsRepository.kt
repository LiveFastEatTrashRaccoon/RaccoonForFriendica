package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Application
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.OAuthToken
import com.livefast.eattrash.raccoonforfriendica.core.api.form.CreateAppForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.ClientApplicationModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import kotlinx.serialization.json.Json

internal class DefaultCredentialsRepository(
    private val provider: ServiceProvider,
    private val json: Json,
    engine: HttpClientEngine = provideHttpClientEngine(),
) : CredentialsRepository {
    private val httpClient =
        HttpClient(engine) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }
        }

    override suspend fun validate(node: String, credentials: ApiCredentials): UserModel? = runCatching {
        provider.changeNode(node)
        provider.setAuth(credentials.toServiceCredentials())
        provider.users.verifyCredentials().toModel()
    }.getOrNull()

    override suspend fun validateNode(node: String): Boolean = runCatching {
        provider.changeNode(node)
        provider.instance.getInfo()
        true
    }.getOrElse { false }

    override suspend fun createApplication(
        node: String,
        clientName: String,
        website: String,
        redirectUri: String,
        scopes: String,
    ): ClientApplicationModel? = runCatching {
        provider.changeNode(node)
        val data =
            CreateAppForm(
                clientName = clientName,
                website = website,
                redirectUris = redirectUri,
                scopes = scopes,
            )
        provider.apps.create(data).toModel()
    }.getOrNull()

    override suspend fun validateApplicationCredentials(node: String, credentials: ApiCredentials): Boolean =
        when (credentials) {
            is ApiCredentials.HttpBasic -> true
            is ApiCredentials.OAuth2 -> {
                runCatching {
                    provider.changeNode(node)
                    provider.setAuth(credentials.toServiceCredentials())
                    provider.apps.verifyCredentials().toModel()
                    true
                }.getOrElse { false }
            }
        }

    override suspend fun exchangeToken(
        node: String,
        path: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        grantType: String,
        code: String,
    ): ApiCredentials? = runCatching {
        val data =
            FormDataContent(
                Parameters.build {
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("redirect_uri", redirectUri)
                    append("grant_type", grantType)
                    append("code", code)
                },
            )
        val url =
            URLBuilder()
                .apply {
                    protocol = URLProtocol.HTTPS
                    host = node
                    path(path)
                }.toString()
        val responseBody =
            httpClient
                .preparePost(url) { setBody(data) }.execute()
                .bodyAsText()
        json.decodeFromString<OAuthToken>(responseBody).toModel()
    }.getOrNull()
}

private fun Application.toModel() = ClientApplicationModel(
    name = name,
    webSite = webSite,
    clientId = clientId,
    clientSecret = clientSecret,
)

private fun CredentialAccount.toModel() = UserModel(
    bot = bot,
    created = createdAt,
    displayName = displayName,
    group = group,
    handle = acct,
    id = id,
    username = username,
    avatar = avatar,
)

private fun OAuthToken.toModel(): ApiCredentials = ApiCredentials.OAuth2(
    accessToken = accessToken,
    refreshToken = refreshToken.orEmpty(),
)
