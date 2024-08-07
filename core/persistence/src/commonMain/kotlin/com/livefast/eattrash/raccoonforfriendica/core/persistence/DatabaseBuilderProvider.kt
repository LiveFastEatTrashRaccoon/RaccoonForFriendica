package com.livefast.eattrash.raccoonforfriendica.core.persistence

import androidx.room.RoomDatabase

internal interface DatabaseBuilderProvider {
    fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
}
