package com.livefast.eattrash.raccoonforfriendica.core.persistence.di

import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.AccountDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DaoFactory
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.DraftDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.SettingsDao
import com.livefast.eattrash.raccoonforfriendica.core.persistence.dao.UserRateLimitDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
class PersistenceModule {

    @Provides
    @SingleIn(AppScope::class)
    fun provideAccountDao(factory: DaoFactory): AccountDao = factory.getAccountDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideSettingsDao(factory: DaoFactory): SettingsDao = factory.getSettingsDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideDraftDao(factory: DaoFactory): DraftDao = factory.getDraftDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideUserRateLimitDao(factory: DaoFactory): UserRateLimitDao = factory.getUserRateLimitDao()
}
