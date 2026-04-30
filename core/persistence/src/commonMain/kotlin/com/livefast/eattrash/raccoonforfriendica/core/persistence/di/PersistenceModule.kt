package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.UserRateLimitDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DatabaseProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DefaultDatabaseProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val persistenceModule =
    DI.Module("PersistenceModule") {
        import(nativePersistenceModule)

        bindSingleton<DatabaseProvider> {
            DefaultDatabaseProvider(
                builderProvider = instance(),
            )
        }
        bindSingleton<AccountDao> {
            val provider = instance<DatabaseProvider>()
            provider.provideDatabase().getAccountDao()
        }
        bindSingleton<SettingsDao> {
            val provider = instance<DatabaseProvider>()
            provider.provideDatabase().getSettingsDao()
        }
        bindSingleton<DraftDao> {
            val provider = instance<DatabaseProvider>()
            provider.provideDatabase().getDraftDao()
        }
        bindSingleton<UserRateLimitDao> {
            val provider = instance<DatabaseProvider>()
            provider.provideDatabase().getUserRateLimitDao()
        }
    }
