package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import org.koin.java.KoinJavaComponent

actual fun getApiConfigurationRepository(): ApiConfigurationRepository {
    val res by KoinJavaComponent.inject<ApiConfigurationRepository>(ApiConfigurationRepository::class.java)
    return res
}

actual fun getSettingsRepository(): SettingsRepository {
    val res by KoinJavaComponent.inject<SettingsRepository>(SettingsRepository::class.java)
    return res
}

actual fun getAuthManager(): AuthManager {
    val res by KoinJavaComponent.inject<AuthManager>(AuthManager::class.java)
    return res
}
