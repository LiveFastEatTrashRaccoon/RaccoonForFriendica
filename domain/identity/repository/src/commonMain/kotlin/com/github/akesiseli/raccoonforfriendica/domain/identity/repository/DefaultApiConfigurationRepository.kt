package com.github.akesiseli.raccoonforfriendica.domain.identity.repository

import com.github.akesiseli.raccoonforfriendica.core.api.provider.ServiceProvider
import com.github.akesiseli.raccoonforfriendica.core.preferences.TemporaryKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private const val KEY_LAST_NODE = "lastInstance"
private const val DEFAULT_NODE = "poliverso.org"

internal class DefaultApiConfigurationRepository(
    private val serviceProvider: ServiceProvider,
    private val keyStore: TemporaryKeyStore,
) : ApiConfigurationRepository {
    override val node = MutableStateFlow("")

    init {
        val instance = keyStore[KEY_LAST_NODE, ""].takeIf { it.isNotEmpty() } ?: DEFAULT_NODE
        changeNode(instance)
    }

    override fun changeNode(value: String) {
        node.update { value }
        serviceProvider.changeNode(value)
        keyStore.save(KEY_LAST_NODE, value)
    }
}
