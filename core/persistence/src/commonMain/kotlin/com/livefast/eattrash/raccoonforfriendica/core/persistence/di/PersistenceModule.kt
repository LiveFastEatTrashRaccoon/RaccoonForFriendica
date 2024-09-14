package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.DatabaseProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.DefaultDatabaseProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import org.koin.dsl.module

val corePersistenceModule =
    module {
        includes(nativePersistenceModule)

        single<DatabaseProvider> {
            DefaultDatabaseProvider(
                builderProvider = get(),
            )
        }

        single<AccountDao> {
            val dbProvider: DatabaseProvider = get()
            dbProvider.provideDatabase().getAccountDao()
        }

        single<SettingsDao> {
            val dbProvider: DatabaseProvider = get()
            dbProvider.provideDatabase().getSettingsDao()
        }
        single<DraftDao> {
            val dbProvider: DatabaseProvider = get()
            dbProvider.provideDatabase().getDraftDao()
        }
    }
