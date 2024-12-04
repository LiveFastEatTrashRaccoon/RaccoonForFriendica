package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DatabaseProvider
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.persistence.provider")
internal class ProviderModule {
    @Single
    fun provideAccountDao(dbProvider: DatabaseProvider): AccountDao = dbProvider.provideDatabase().getAccountDao()

    @Single
    fun provideSettingsDao(dbProvider: DatabaseProvider): SettingsDao = dbProvider.provideDatabase().getSettingsDao()

    @Single
    fun provideDraftDao(dbProvider: DatabaseProvider): DraftDao = dbProvider.provideDatabase().getDraftDao()
}

@Module(includes = [BuilderModule::class, ProviderModule::class])
internal class PersistenceModule

val corePersistenceModule = PersistenceModule().module
