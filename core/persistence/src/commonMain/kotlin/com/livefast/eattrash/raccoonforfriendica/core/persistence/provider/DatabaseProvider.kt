package com.livefast.eattrash.raccoonforfriendica.core.persistence.provider

import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase

interface DatabaseProvider {
    fun provideDatabase(): AppDatabase
}
