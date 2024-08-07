package com.livefast.eattrash.raccoonforfriendica.core.persistence

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

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
