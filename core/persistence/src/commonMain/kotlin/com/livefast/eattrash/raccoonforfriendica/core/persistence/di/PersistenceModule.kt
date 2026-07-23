package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.UserRateLimitDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DatabaseProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DefaultDatabaseProvider
import org.koin.dsl.module

val persistenceModule = module {
    includes(nativePersistenceModule)

    single<DatabaseProvider> {
        DefaultDatabaseProvider(
            builderProvider = get(),
        )
    }
    single<AccountDao> {
        val provider = get<DatabaseProvider>()
        provider.provideDatabase().getAccountDao()
    }
    single<SettingsDao> {
        val provider = get<DatabaseProvider>()
        provider.provideDatabase().getSettingsDao()
    }
    single<DraftDao> {
        val provider = get<DatabaseProvider>()
        provider.provideDatabase().getDraftDao()
    }
    single<UserRateLimitDao> {
        val provider = get<DatabaseProvider>()
        provider.provideDatabase().getUserRateLimitDao()
    }
}
