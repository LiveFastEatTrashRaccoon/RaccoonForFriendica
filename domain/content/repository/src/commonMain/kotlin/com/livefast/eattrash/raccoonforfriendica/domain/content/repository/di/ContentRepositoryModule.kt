package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultUserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainContentRepositoryModule =
    module {
        single<TimelineRepository> {
            DefaultTimelineRepository(
                provider = get(named("default")),
            )
        }
        single<TimelineEntryRepository> {
            DefaultTimelineEntryRepository(
                provider = get(named("default")),
            )
        }
        single<UserRepository> {
            DefaultUserRepository(
                provider = get(named("default")),
            )
        }
        single<NotificationRepository> {
            DefaultNotificationRepository(
                provider = get(named("default")),
            )
        }
        single<TrendingRepository> {
            DefaultTrendingRepository(
                provider = get(named("default")),
            )
        }
        single<TagRepository> {
            DefaultTagRepository(
                provider = get(named("default")),
            )
        }
        single<PhotoRepository> {
            DefaultPhotoRepository(
                provider = get(named("default")),
            )
        }
    }
