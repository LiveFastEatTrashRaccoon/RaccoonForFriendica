package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import androidx.room.Room
import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal class DefaultDatabaseBuilderProvider : DatabaseBuilderProvider {
    override fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = documentDirectory() + "/raccoonforfriendica.db"
        return Room
            .databaseBuilder<AppDatabase>(
                name = dbFilePath,
            )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory =
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
        return requireNotNull(documentDirectory?.path)
    }
}
