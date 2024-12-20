package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.builder.DatabaseBuilderProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.builder.DefaultDatabaseBuilderProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal actual val nativePersistenceModule =
    DI.Module("NativePersistenceModule") {
        bind<DatabaseBuilderProvider> {
            singleton {
                DefaultDatabaseBuilderProvider(context = instance())
            }
        }
    }
