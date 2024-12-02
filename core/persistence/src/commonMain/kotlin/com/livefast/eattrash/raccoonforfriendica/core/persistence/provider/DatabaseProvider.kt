package com.livefast.eattrash.raccoonforfriendica.core.persistence.provider

import com.livefast.eattrash.raccoonforfriendica.core.persistence.AppDatabase

internal interface DatabaseProvider {
    fun provideDatabase(): AppDatabase
}
