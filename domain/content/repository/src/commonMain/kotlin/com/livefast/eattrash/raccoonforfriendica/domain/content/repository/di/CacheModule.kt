package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.DefaultLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
class CacheModule {
    @Provides
    @SingleIn(AppScope::class)
    fun userCache(): LocalItemCache<UserModel> = DefaultLocalItemCache()

    @Provides
    @SingleIn(AppScope::class)
    fun timelineEntryCache(): LocalItemCache<TimelineEntryModel> = DefaultLocalItemCache()

    @Provides
    @SingleIn(AppScope::class)
    fun eventCache(): LocalItemCache<EventModel> = DefaultLocalItemCache()

    @Provides
    @SingleIn(AppScope::class)
    fun circleCache(): LocalItemCache<CircleModel> = DefaultLocalItemCache()
}
