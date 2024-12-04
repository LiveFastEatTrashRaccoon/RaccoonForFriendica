package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import org.koin.core.annotation.Single

@Single
internal actual class DefaultDatabaseBuilderProvider(
    private val context: Context,
) : DatabaseBuilderProvider {
    actual override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath("raccoonforfriendica.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
}
