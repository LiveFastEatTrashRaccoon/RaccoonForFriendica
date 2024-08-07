package com.livefast.eattrash.raccoonforfriendica.core.persistence

internal interface DatabaseProvider {
    fun provideDatabase(): AppDatabase
}
