package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getApiConfigurationRepository(): ApiConfigurationRepository = DomainIdentityRepositoryDiHelper.apiConfigurationRepository

actual fun getSettingsRepository(): SettingsRepository = DomainIdentityRepositoryDiHelper.settingsRepository

actual fun getAuthManager(): AuthManager = DomainIdentityRepositoryDiHelper.authManager

actual fun getEntryActionRepository(): EntryActionRepository = DomainIdentityRepositoryDiHelper.entryActionRepository

internal object DomainIdentityRepositoryDiHelper : KoinComponent {
    val settingsRepository: SettingsRepository by inject()
    val authManager: AuthManager by inject()
    val apiConfigurationRepository: ApiConfigurationRepository by inject()
    val entryActionRepository: EntryActionRepository by inject()
}
