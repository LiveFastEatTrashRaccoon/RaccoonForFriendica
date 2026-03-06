package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import androidx.room.Room
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import java.io.File

internal class DefaultDatabaseBuilderProvider : DatabaseBuilderProvider {
    override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "raccoonforfriendica.db")
        return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
    }
}
