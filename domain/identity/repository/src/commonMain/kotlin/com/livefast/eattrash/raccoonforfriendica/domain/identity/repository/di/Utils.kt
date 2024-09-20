package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.EntryActionRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

expect fun getApiConfigurationRepository(): ApiConfigurationRepository

expect fun getSettingsRepository(): SettingsRepository

expect fun getAuthManager(): AuthManager

expect fun getEntryActionRepository(): EntryActionRepository
