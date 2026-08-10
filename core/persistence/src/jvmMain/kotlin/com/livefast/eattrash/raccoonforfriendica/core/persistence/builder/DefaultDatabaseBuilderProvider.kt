package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import androidx.room.Room
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import org.koin.core.annotation.Single
import java.io.File

@Single
internal actual class DefaultDatabaseBuilderProvider : DatabaseBuilderProvider {
    actual override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "raccoonforfriendica.db")
        return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
    }
}
