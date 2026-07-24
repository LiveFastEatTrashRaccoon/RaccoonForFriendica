package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultAccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultAccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultCredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultIdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultInstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultStopWordRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val identityRepositoryModule = module {
    single<AccountCredentialsCache> {
        DefaultAccountCredentialsCache(
            keyStore = get(),
        )
    }
    single<AccountRepository> {
        DefaultAccountRepository(
            accountDao = get(),
        )
    }
    single<ApiConfigurationRepository> {
        DefaultApiConfigurationRepository(
            provider = get(named("default")),
            keyStore = get(),
            credentialsRepository = get(),
            authManager = get(),
        )
    }
    single<CredentialsRepository> {
        DefaultCredentialsRepository(
            provider = get(named("other")),
            json = get(),
        )
    }
    single<IdentityRepository> {
        DefaultIdentityRepository(
            provider = get(named("default")),
        )
    }
    single<ImageAutoloadObserver> {
        DefaultImageAutoloadObserver(
            networkStateObserver = get(),
            settingsRepository = get(),
        )
    }
    single<SettingsRepository> {
        DefaultSettingsRepository(
            settingsDao = get(),
        )
    }
    single<StopWordRepository> {
        DefaultStopWordRepository(
            keyStore = get(),
        )
    }
    single<InstanceShortcutRepository> {
        DefaultInstanceShortcutRepository(
            keyStore = get(),
        )
    }
}
