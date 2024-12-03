package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.ksp.generated.module

@Module
@ComponentScan("com.livefast.eattrash.raccoonforfriendica.domain.content.repository")
internal class ContentRepositoryModule {
    @Single
    fun provideLocalItemCacheUserModel(): LocalItemCache<UserModel> = DefaultLocalItemCache()

    @Single
    fun provideLocalItemCacheTimelineEntryModel(): LocalItemCache<TimelineEntryModel> = DefaultLocalItemCache()

    @Single
    fun provideLocalItemCacheEventModel(): LocalItemCache<EventModel> = DefaultLocalItemCache()

    @Single
    fun provideLocalItemCacheCircleModel(): LocalItemCache<CircleModel> = DefaultLocalItemCache()

    @Single
    fun provideLocalItemCacheScheduledEntryRepository(): LocalItemCache<ScheduledEntryRepository> = DefaultLocalItemCache()
}

val domainContentRepositoryModule = ContentRepositoryModule().module
