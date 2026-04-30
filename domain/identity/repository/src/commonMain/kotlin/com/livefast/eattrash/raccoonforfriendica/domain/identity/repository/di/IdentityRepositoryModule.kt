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
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val identityRepositoryModule =
    DI.Module("IdentityRepositoryModule") {
        bindSingleton<AccountCredentialsCache> {
            DefaultAccountCredentialsCache(
                keyStore = instance(),
            )
        }
        bindSingleton<AccountRepository> {
            DefaultAccountRepository(
                accountDao = instance(),
            )
        }
        bindSingleton<ApiConfigurationRepository> {
            DefaultApiConfigurationRepository(
                provider = instance(tag = "default"),
                keyStore = instance(),
                credentialsRepository = instance(),
                authManager = instance(),
            )
        }
        bindSingleton<CredentialsRepository> {
            DefaultCredentialsRepository(
                provider = instance(tag = "other"),
                engine = instance(),
                json = instance(),
            )
        }
        bindSingleton<IdentityRepository> {
            DefaultIdentityRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<ImageAutoloadObserver> {
            DefaultImageAutoloadObserver(
                networkStateObserver = instance(),
                settingsRepository = instance(),
            )
        }
        bindSingleton<SettingsRepository> {
            DefaultSettingsRepository(
                settingsDao = instance(),
            )
        }
        bindSingleton<StopWordRepository> {
            DefaultStopWordRepository(
                keyStore = instance(),
            )
        }
        bindSingleton<InstanceShortcutRepository> {
            DefaultInstanceShortcutRepository(
                keyStore = instance(),
            )
        }
    }
