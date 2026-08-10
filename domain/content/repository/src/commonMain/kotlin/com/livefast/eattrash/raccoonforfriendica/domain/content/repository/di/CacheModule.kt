package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.CircleLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.EventLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.ScheduledEntryLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.TimelineEntryLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.UserLocalItemCache
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
internal class UserCacheModule {
    @Single(binds = [LocalItemCache::class])
    fun userCache() = UserLocalItemCache()
}

@Module
internal class TimelineEntryCacheModule {
    @Single(binds = [LocalItemCache::class])
    fun timelineEntryCache() = TimelineEntryLocalItemCache()
}

@Module
internal class EventCacheModule {
    @Single(binds = [LocalItemCache::class])
    fun eventCache() = EventLocalItemCache()
}

@Module
internal class CircleCacheModule {
    @Single(binds = [LocalItemCache::class])
    fun circleCache() = CircleLocalItemCache()
}

@Module
internal class ScheduledEntryCacheModule {
    @Single(binds = [LocalItemCache::class])
    fun scheduledEntryCache() = ScheduledEntryLocalItemCache()
}

@Module(
    includes = [
        UserCacheModule::class,
        TimelineEntryCacheModule::class,
        EventCacheModule::class,
        CircleCacheModule::class,
        ScheduledEntryCacheModule::class,
    ],
)
class CacheModule
