package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.builder.DatabaseBuilderProvider
import com.livefast.eattrash.raccoonforfriendica.core.persistence.builder.DefaultDatabaseBuilderProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal actual val nativePersistenceModule =
    DI.Module("NativePersistenceModule") {
        bindSingleton<DatabaseBuilderProvider> {
            DefaultDatabaseBuilderProvider(context = instance())
        }
    }
