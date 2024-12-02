package com.livefast.eattrash.raccoonforfriendica.core.persistence.builder

import androidx.room.RoomDatabase
import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase

internal interface DatabaseBuilderProvider {
    fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}
