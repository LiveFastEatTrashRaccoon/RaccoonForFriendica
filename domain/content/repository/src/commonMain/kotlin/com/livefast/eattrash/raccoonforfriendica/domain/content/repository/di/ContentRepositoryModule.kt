package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultCirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultInboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultSearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultUserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
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
        single<SearchRepository> {
            DefaultSearchRepository(
                provider = get(named("default")),
            )
        }
        single<CirclesRepository> {
            DefaultCirclesRepository(
                provider = get(named("default")),
            )
        }
        single<InboxManager> {
            DefaultInboxManager(
                notificationRepository = get(),
            )
        }
        single<NodeInfoRepository> {
            DefaultNodeInfoRepository(
                provider = get(named("default")),
            )
        }
    }
