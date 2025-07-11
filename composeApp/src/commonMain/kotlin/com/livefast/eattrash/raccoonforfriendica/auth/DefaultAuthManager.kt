package com.livefast.eattrash.raccoonforfriendica.auth

import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.toInt
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.path
import kotlinx.coroutines.flow.MutableSharedFlow

internal class DefaultAuthManager(
    private val navigationCoordinator: NavigationCoordinator,
    private val credentialsRepository: CredentialsRepository,
    private val keyStore: TemporaryKeyStore,
) : AuthManager {
    override val credentialFlow = MutableSharedFlow<ApiCredentials>()

    override fun openLogin(type: LoginType) {
        navigationCoordinator.push(Destination.Login(type.toInt()))
    }

    override fun openLegacyLogin() {
        navigationCoordinator.push(Destination.LegacyLogin)
    }

    override fun openNewAccount() {
        navigationCoordinator.push(Destination.NewAccount)
    }

    override suspend fun startOAuthFlow(node: String): String {
        val app =
            credentialsRepository.createApplication(
                node = node,
                clientName = CLIENT_NAME,
                website = WEBSITE,
                scopes = SCOPES,
                redirectUri = REDIRECT_URI,
            )
        checkNotNull(app) { "Unable to create remote application" }
        val clientId = app.clientId
        val clientSecret = app.clientSecret
        check(!clientId.isNullOrEmpty()) { "Invalid client ID" }
        check(!clientSecret.isNullOrEmpty()) { "Invalid client secret" }

        storeInitialData(node = node, clientId = clientId, clientSecret = clientSecret)

        val res =
            URLBuilder()
                .apply {
                    protocol = URLProtocol.HTTPS
                    host = node
                    path(AUTH_ENDPOINT)
                    parameters.apply {
                        append("response_type", RESPONSE_TYPE)
                        append("client_id", clientId)
                        append("client_secret", clientSecret)
                        append("redirect_uri", REDIRECT_URI)
                        append("scope", SCOPES)
                    }
                }.buildString()
        return res
    }

    override suspend fun performTokenExchange(url: String) {
        val queryParams = Url(url).parameters
        val code = queryParams["code"].orEmpty()
        val node = retrieveNode()
        val clientId = retrieveClientId()
        val clientSecret = retrieveClientSecret()
        require(code.isNotEmpty()) { "Invalid code received" }
        check(node.isNotEmpty()) { "Unable to retrieve node name" }
        check(clientId.isNotEmpty()) { "Unable to retrieve client ID" }
        check(clientSecret.isNotEmpty()) { "Unable to retrieve client secret" }

        val credentials =
            credentialsRepository.exchangeToken(
                node = node,
                path = TOKEN_ENDPOINT,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUri = REDIRECT_URI,
                grantType = GRANT_TYPE,
                code = code,
            )
        if (credentials != null) {
            credentialFlow.emit(credentials)
        }
    }

    private suspend fun storeInitialData(node: String, clientId: String, clientSecret: String) {
        keyStore.save(KEY_LAST_NODE, node)
        keyStore.save(KEY_CLIENT_ID, clientId)
        keyStore.save(KEY_CLIENT_SECRET, clientSecret)
    }

    private suspend fun retrieveNode(): String = keyStore.get(KEY_LAST_NODE, "")

    private suspend fun retrieveClientId(): String = keyStore.get(KEY_CLIENT_ID, "")

    private suspend fun retrieveClientSecret(): String = keyStore.get(KEY_CLIENT_SECRET, "")

    companion object {
        private const val AUTH_ENDPOINT = "oauth/authorize"
        private const val TOKEN_ENDPOINT = "oauth/token"
        private const val CLIENT_NAME = "RaccoonForFriendica"
        private const val WEBSITE = "https://github.com/LiveFastEatTrashRaccoon/RaccoonForFriendica"
        private const val RESPONSE_TYPE = "code"
        private const val SCOPES = "read write follow push"
        const val REDIRECT_SCHEME = "raccoonforfriendica"
        const val REDIRECT_HOST = "auth"
        private const val REDIRECT_URI = "$REDIRECT_SCHEME://$REDIRECT_HOST"
        private const val GRANT_TYPE = "authorization_code"
        private const val KEY_CLIENT_ID = "lastAuthClientId"
        private const val KEY_CLIENT_SECRET = "lastAuthClientSecret"
        private const val KEY_LAST_NODE = "lastAuthInstance"
    }
}
