package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getSettingsRepository(): SettingsRepository = DomainIdentityRepositoryDiHelper.settingsRepository

internal object DomainIdentityRepositoryDiHelper : KoinComponent {
    val settingsRepository: SettingsRepository by inject()
}
