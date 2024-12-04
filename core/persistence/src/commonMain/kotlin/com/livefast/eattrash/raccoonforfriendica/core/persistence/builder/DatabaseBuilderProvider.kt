package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import org.koin.core.annotation.Single

internal interface DatabaseBuilderProvider {
    fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}

@Single
internal expect class DefaultDatabaseBuilderProvider : DatabaseBuilderProvider {
    override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}
