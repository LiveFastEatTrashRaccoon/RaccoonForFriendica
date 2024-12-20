package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import org.kodein.di.instance

fun getApiConfigurationRepository(): ApiConfigurationRepository {
    val res by RootDI.di.instance<ApiConfigurationRepository>()
    return res
}

fun getSettingsRepository(): SettingsRepository {
    val res by RootDI.di.instance<SettingsRepository>()
    return res
}

fun getAuthManager(): AuthManager {
    val res by RootDI.di.instance<AuthManager>()
    return res
}
