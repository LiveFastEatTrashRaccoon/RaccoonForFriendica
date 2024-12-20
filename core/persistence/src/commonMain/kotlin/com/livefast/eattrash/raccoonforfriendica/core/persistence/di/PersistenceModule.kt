package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DatabaseProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DefaultDatabaseProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val persistenceModule =
    DI.Module("PersistenceModule") {
        import(nativePersistenceModule)

        bind<DatabaseProvider> {
            singleton {
                DefaultDatabaseProvider(
                    builderProvider = instance(),
                )
            }
        }
        bind<AccountDao> {
            singleton {
                val provider = instance<DatabaseProvider>()
                provider.provideDatabase().getAccountDao()
            }
        }
        bind<SettingsDao> {
            singleton {
                val provider = instance<DatabaseProvider>()
                provider.provideDatabase().getSettingsDao()
            }
        }
        bind<DraftDao> {
            singleton {
                val provider = instance<DatabaseProvider>()
                provider.provideDatabase().getDraftDao()
            }
        }
    }
