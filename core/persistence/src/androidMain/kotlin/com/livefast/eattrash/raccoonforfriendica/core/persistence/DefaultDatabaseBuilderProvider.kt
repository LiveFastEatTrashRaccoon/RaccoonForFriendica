package com.livefast.eattrash.raccoonforfriendica.core.persistence

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DefaultDatabaseBuilderProvider(
    private val context: Context,
) : DatabaseBuilderProvider {
    override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("raccoonforfriendica.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
}
