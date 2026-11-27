package com.livefast.eattrash.raccoonforfriendica.core.persistence.provider

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.builder.DatabaseBuilderProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultDatabaseProvider(
    private val builderProvider: DatabaseBuilderProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DatabaseProvider {
    private val database: AppDatabase by lazy {
        builderProvider
            .provideDatabaseBuilder()
            .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(dispatcher)
            .build()
    }

    override fun provideDatabase(): AppDatabase = database
}
