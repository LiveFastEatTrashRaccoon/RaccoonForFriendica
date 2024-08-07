package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.DatabaseBuilderProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.DefaultDatabaseBuilderProvider
import org.koin.dsl.module

actual val nativePersistenceModule =
    module {
        single<DatabaseBuilderProvider> {
            DefaultDatabaseBuilderProvider()
        }
    }
