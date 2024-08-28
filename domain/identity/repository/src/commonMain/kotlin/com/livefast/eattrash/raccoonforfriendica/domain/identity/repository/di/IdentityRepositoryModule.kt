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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.MutableIdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

val domainIdentityRepositoryModule =
    module {
        single<ApiConfigurationRepository> {
            DefaultApiConfigurationRepository(
                serviceProvider = get(named("default")),
                keyStore = get(),
                identityRepository = get(),
                credentialsRepository = get(),
            )
        }

        single {
            DefaultIdentityRepository(
                accountRepository = get(),
                provider = get(named("default")),
            )
        } binds arrayOf(IdentityRepository::class, MutableIdentityRepository::class)

        single<CredentialsRepository> {
            DefaultCredentialsRepository(
                provider = get(named("other")),
            )
        }

        single<AccountRepository> {
            DefaultAccountRepository(
                accountDao = get(),
            )
        }

        single<SettingsRepository> {
            DefaultSettingsRepository(
                settingsDao = get(),
            )
        }
        single<AccountCredentialsCache> {
            DefaultAccountCredentialsCache(
                keyStore = get(),
            )
        }
    }
