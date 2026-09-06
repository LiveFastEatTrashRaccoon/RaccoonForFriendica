package com.livefast.eattrash.raccoonforfriendica.core.persistence.dao

import com.livefast.eattrash.raccoonforfriendica.core.persistence.provider.DatabaseProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultDaoFactory(provider: DatabaseProvider) : DaoFactory {
    private val appDb by lazy { provider.provideDatabase() }

    override fun getAccountDao(): AccountDao = appDb.getAccountDao()

    override fun getSettingsDao(): SettingsDao = appDb.getSettingsDao()

    override fun getDraftDao(): DraftDao = appDb.getDraftDao()

    override fun getUserRateLimitDao(): UserRateLimitDao = appDb.getUserRateLimitDao()
}
