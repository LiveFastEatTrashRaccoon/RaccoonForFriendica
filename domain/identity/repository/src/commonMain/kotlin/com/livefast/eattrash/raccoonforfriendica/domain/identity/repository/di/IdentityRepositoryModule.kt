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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultSettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.DefaultStopWordRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.StopWordRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val identityRepositoryModule =
    DI.Module("IdentityRepositoryModule") {
        bind<AccountCredentialsCache> {
            singleton {
                DefaultAccountCredentialsCache(
                    keyStore = instance(),
                )
            }
        }
        bind<AccountRepository> {
            singleton {
                DefaultAccountRepository(
                    accountDao = instance(),
                )
            }
        }
        bind<ApiConfigurationRepository> {
            singleton {
                DefaultApiConfigurationRepository(
                    provider = instance(tag = "default"),
                    keyStore = instance(),
                    credentialsRepository = instance(),
                )
            }
        }
        bind<CredentialsRepository> {
            singleton {
                DefaultCredentialsRepository(
                    provider = instance(tag = "other"),
                    engine = instance(),
                )
            }
        }
        bind<IdentityRepository> {
            singleton {
                DefaultIdentityRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<ImageAutoloadObserver> {
            singleton {
                DefaultImageAutoloadObserver(
                    networkStateObserver = instance(),
                    settingsRepository = instance(),
                )
            }
        }
        bind<SettingsRepository> {
            singleton {
                DefaultSettingsRepository(
                    settingsDao = instance(),
                )
            }
        }
        bind<StopWordRepository> {
            singleton {
                DefaultStopWordRepository(
                    keyStore = instance(),
                )
            }
        }
    }
