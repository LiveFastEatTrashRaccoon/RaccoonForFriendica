package com.livefast.eattrash.raccoonforfriendica.core.persistence

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity

@Database(
    entities = [
        AccountEntity::class,
        SettingsEntity::class,
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),

    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao

    abstract fun getSettingsDao(): SettingsDao
}
