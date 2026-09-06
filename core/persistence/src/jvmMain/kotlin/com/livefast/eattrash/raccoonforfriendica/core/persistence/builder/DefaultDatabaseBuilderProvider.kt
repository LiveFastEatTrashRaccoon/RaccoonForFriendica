package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import androidx.room.Room
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.io.File

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultDatabaseBuilderProvider : DatabaseBuilderProvider {
    override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "raccoonforfriendica.db")
        return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
    }
}
