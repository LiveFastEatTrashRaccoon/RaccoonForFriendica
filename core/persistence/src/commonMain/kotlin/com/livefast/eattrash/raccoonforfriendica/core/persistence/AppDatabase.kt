package com.livefast.eattrash.raccoonforfriendica.core.persistence

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.AccountEntity
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.DraftEntity
import com.livefast.eattrash.raccoonforfriendica.core.persistence.entities.SettingsEntity

@Database(
    entities = [
        AccountEntity::class,
        SettingsEntity::class,
        DraftEntity::class,
    ],
    version = 9,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ],
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao

    abstract fun getSettingsDao(): SettingsDao

    abstract fun getDraftDao(): DraftDao
}
