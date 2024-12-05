package com.livefast.eattrash.raccoonforfriendica.core.api.di

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo.AppInfoRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
class ApiModule {
    @Single
    @Named("default")
    fun provideLocalServiceProvider(appInfoRepository: AppInfoRepository): ServiceProvider =
        DefaultServiceProvider(appInfoRepository = appInfoRepository)

    @Single
    @Named("other")
    fun provideOtherServiceProvider(appInfoRepository: AppInfoRepository): ServiceProvider =
        DefaultServiceProvider(appInfoRepository = appInfoRepository)
}
