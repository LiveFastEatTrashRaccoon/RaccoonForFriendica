package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultApiConfigurationRepository(
    private val provider: ServiceProvider,
    private val keyStore: TemporaryKeyStore,
    private val credentialsRepository: CredentialsRepository,
) : ApiConfigurationRepository {
    override val node = MutableStateFlow("")
    override val isLogged = MutableStateFlow(false)

    override val defaultNode: String
        get() = keyStore[KEY_LAST_NODE, ""].takeIf { it.isNotEmpty() } ?: DEFAULT_NODE

    override fun changeNode(value: String) {
        node.update { value }
        provider.changeNode(value)
        keyStore.save(KEY_LAST_NODE, value)
    }

    override fun setAuth(credentials: ApiCredentials?) {
        val serviceCredentials = credentials?.toServiceCredentials()
        provider.setAuth(serviceCredentials)
        if (credentials != null) {
            saveInKeyStore(credentials)
            isLogged.update { true }
        } else {
            clearKeyStore()
            isLogged.update { false }
        }
    }

    override suspend fun hasCachedAuthCredentials(): Boolean {
        val node = keyStore[KEY_LAST_NODE, ""].takeIf { it.isNotEmpty() } ?: DEFAULT_NODE
        val credentials = retrieveFromKeyStore()
        return validateCredentials(credentials = credentials, node = node)
    }

    private suspend fun validateCredentials(
        credentials: ApiCredentials?,
        node: String,
    ): Boolean =
        withTimeoutOrNull(VALIDATE_CREDENTIALS_TIMEOUT) {
            credentials != null &&
                credentialsRepository.validateApplicationCredentials(
                    node = node,
                    credentials = credentials,
                )
        } ?: false

    private fun retrieveFromKeyStore(): ApiCredentials? {
        val method = keyStore[KEY_METHOD, DEFAULT_METHOD]
        return when (method) {
            METHOD_BASIC -> {
                val user = keyStore[KEY_CRED_1, ""]
                val pass = keyStore[KEY_CRED_2, ""]
                if (user.isNotEmpty() && pass.isNotEmpty()) {
                    ApiCredentials.HttpBasic(user, pass)
                } else {
                    null
                }
            }

            METHOD_OAUTH_2 -> {
                val accessToken = keyStore[KEY_CRED_1, ""]
                val refreshToken = keyStore[KEY_CRED_2, ""]
                if (accessToken.isNotEmpty()) {
                    ApiCredentials.OAuth2(accessToken, refreshToken)
                } else {
                    null
                }
            }

            else -> null
        }
    }

    private fun saveInKeyStore(credentials: ApiCredentials) {
        when (credentials) {
            is ApiCredentials.HttpBasic -> {
                keyStore.save(KEY_CRED_1, credentials.user)
                keyStore.save(KEY_CRED_2, credentials.pass)
                keyStore.save(KEY_METHOD, METHOD_BASIC)
            }

            is ApiCredentials.OAuth2 -> {
                keyStore.save(KEY_CRED_1, credentials.accessToken)
                keyStore.save(KEY_CRED_2, credentials.refreshToken)
                keyStore.save(KEY_METHOD, METHOD_OAUTH_2)
            }
        }
    }

    private fun clearKeyStore() {
        keyStore.remove(KEY_CRED_1)
        keyStore.remove(KEY_CRED_2)
    }

    companion object {
        private const val KEY_LAST_NODE = "lastInstance"
        private const val KEY_CRED_1 = "lastCred1"
        private const val KEY_CRED_2 = "lastCred2"
        private const val KEY_METHOD = "lastMethod"
        private const val METHOD_BASIC = "HTTPBasic"
        private const val METHOD_OAUTH_2 = "OAuth2"
        private const val DEFAULT_NODE = "poliverso.org"
        private const val DEFAULT_METHOD = METHOD_BASIC
        private const val VALIDATE_CREDENTIALS_TIMEOUT = 2000L
    }
}
