package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultCirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultDirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultDraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultInboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultMediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultReportRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultScheduledEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultSearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultSupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultUserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReportRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
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
        single<DirectMessageRepository> {
            DefaultDirectMessageRepository(
                provider = get(named("default")),
            )
        }
        single<PhotoAlbumRepository> {
            DefaultPhotoAlbumRepository(
                provider = get(named("default")),
            )
        }
        single<LocalItemCache<UserModel>> {
            DefaultLocalItemCache()
        }
        single<LocalItemCache<TimelineEntryModel>> {
            DefaultLocalItemCache()
        }
        single<ScheduledEntryRepository> {
            DefaultScheduledEntryRepository(
                provider = get(named("default")),
            )
        }
        single<DraftRepository> {
            DefaultDraftRepository(
                draftDao = get(),
                provider = get(named("default")),
            )
        }
        single<MediaRepository> {
            DefaultMediaRepository(
                provider = get(named("default")),
            )
        }
        single<ReportRepository> {
            DefaultReportRepository(
                provider = get(named("default")),
            )
        }
        single<SupportedFeatureRepository> {
            DefaultSupportedFeatureRepository(
                nodeInfoRepository = get(),
            )
        }
        single<EmojiRepository> {
            DefaultEmojiRepository(
                provider = get(named("default")),
                otherProvider = get(named("other")),
            )
        }
        single<EmojiHelper> {
            DefaultEmojiHelper(
                repository = get(),
            )
        }
    }
