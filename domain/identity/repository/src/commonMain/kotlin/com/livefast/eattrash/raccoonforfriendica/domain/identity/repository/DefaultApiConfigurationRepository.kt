package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.preferences.TemporaryKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private const val KEY_LAST_NODE = "lastInstance"
private const val KEY_CRED_1 = "lastCred1"
private const val KEY_CRED_2 = "lastCred2"
private const val DEFAULT_NODE = "poliverso.org"

internal class DefaultApiConfigurationRepository(
    private val serviceProvider: ServiceProvider,
    private val keyStore: TemporaryKeyStore,
) : ApiConfigurationRepository {
    override val node = MutableStateFlow("")
    override val isLogged = MutableStateFlow(false)

    init {
        val instance = keyStore[KEY_LAST_NODE, ""].takeIf { it.isNotEmpty() } ?: DEFAULT_NODE
        changeNode(instance)

        val credentials = keyStore[KEY_CRED_1, ""] to keyStore[KEY_CRED_2, ""]
        setAuth(credentials.takeIf { it.first.isNotEmpty() && it.second.isNotEmpty() })
    }

    override fun changeNode(value: String) {
        node.update { value }
        serviceProvider.changeNode(value)
        keyStore.save(KEY_LAST_NODE, value)
        setAuth(null)
    }

    override fun setAuth(credentials: Pair<String, String>?) {
        serviceProvider.setAuth(credentials)
        if (credentials != null) {
            keyStore.save(KEY_CRED_1, credentials.first)
            keyStore.save(KEY_CRED_2, credentials.second)
        }
        isLogged.update { credentials != null }
    }
}
