package com.livefast.eattrash.raccoonforfriendica.core.persistence.provider

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.builder.DatabaseBuilderProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Single

@Single
internal class DefaultDatabaseProvider(
    private val builderProvider: DatabaseBuilderProvider,
) : DatabaseProvider {
    private val database: AppDatabase by lazy {
        builderProvider
            .provideDatabaseBuilder()
            .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    override fun provideDatabase(): AppDatabase = database
}
