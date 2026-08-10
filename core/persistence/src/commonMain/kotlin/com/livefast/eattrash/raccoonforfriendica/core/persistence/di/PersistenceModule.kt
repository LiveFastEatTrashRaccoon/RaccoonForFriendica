package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DaoFactory
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.core.persistence")
class PersistenceModule {

    @Single
    fun accountDao(factory: DaoFactory) = factory.getAccountDao()

    @Single
    fun settingsDao(factory: DaoFactory) = factory.getSettingsDao()

    @Single
    fun draftDao(factory: DaoFactory) = factory.getDraftDao()

    @Single
    fun userRateLimitDao(factory: DaoFactory) = factory.getUserRateLimitDao()
}
