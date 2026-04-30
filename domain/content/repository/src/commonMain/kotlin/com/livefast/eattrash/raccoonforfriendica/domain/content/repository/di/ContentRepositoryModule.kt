package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementsManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAnnouncementRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAnnouncementsManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultCirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultDirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultDraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEventRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultFallbackTranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultFollowedHashtagCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultInboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultLocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultMarkerRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultMediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultPushNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultReportRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultScheduledEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultSearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultSupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultUserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultUserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EventRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FallbackTranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FollowedHashtagCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MarkerRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.MediaRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PushNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReplyHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ReportRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.ScheduledEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TagRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendingRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRateLimitRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

internal val cacheModule =
    DI.Module("ContentRepositoryCacheModule") {
        bindSingleton<LocalItemCache<UserModel>> {
            DefaultLocalItemCache()
        }
        bindSingleton<LocalItemCache<TimelineEntryModel>> {
            DefaultLocalItemCache()
        }
        bindSingleton<LocalItemCache<EventModel>> {
            DefaultLocalItemCache()
        }
        bindSingleton<LocalItemCache<CircleModel>> {
            DefaultLocalItemCache()
        }
        bindSingleton<LocalItemCache<ScheduledEntryRepository>> {
            DefaultLocalItemCache()
        }
    }

val contentRepositoryModule =
    DI.Module("ContentRepositoryModule") {
        import(cacheModule)

        bindSingleton<AnnouncementRepository> {
            DefaultAnnouncementRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<AnnouncementsManager> {
            DefaultAnnouncementsManager(
                announcementRepository = instance(),
            )
        }
        bindSingleton<CirclesRepository> {
            DefaultCirclesRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<DirectMessageRepository> {
            DefaultDirectMessageRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<DraftRepository> {
            DefaultDraftRepository(
                draftDao = instance(),
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<EmojiHelper> {
            DefaultEmojiHelper(
                repository = instance(),
            )
        }
        bindSingleton<EmojiRepository> {
            DefaultEmojiRepository(
                provider = instance(tag = "default"),
                otherProvider = instance(tag = "other"),
            )
        }
        bindSingleton<EventRepository> {
            DefaultEventRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<InboxManager> {
            DefaultInboxManager(
                notificationRepository = instance(),
                markerRepository = instance(),
            )
        }
        bindSingleton<MarkerRepository> {
            DefaultMarkerRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<MediaRepository> {
            DefaultMediaRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<NodeInfoRepository> {
            DefaultNodeInfoRepository(
                provider = instance(tag = "default"),
                json = instance(),
            )
        }
        bindSingleton<NotificationRepository> {
            DefaultNotificationRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<PhotoAlbumRepository> {
            DefaultPhotoAlbumRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<PhotoRepository> {
            DefaultPhotoRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<PushNotificationRepository> {
            DefaultPushNotificationRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<ReplyHelper> {
            DefaultReplyHelper(
                entryRepository = instance(),
            )
        }
        bindSingleton<ReportRepository> {
            DefaultReportRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<ScheduledEntryRepository> {
            DefaultScheduledEntryRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<SearchRepository> {
            DefaultSearchRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<SupportedFeatureRepository> {
            DefaultSupportedFeatureRepository(
                nodeInfoRepository = instance(),
            )
        }
        bindSingleton<TagRepository> {
            DefaultTagRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<TimelineEntryRepository> {
            DefaultTimelineEntryRepository(
                provider = instance(tag = "default"),
                otherProvider = instance(tag = "other"),
            )
        }
        bindSingleton<TimelineRepository> {
            DefaultTimelineRepository(
                provider = instance(tag = "default"),
                otherProvider = instance(tag = "other"),
            )
        }
        bindSingleton<TrendingRepository> {
            DefaultTrendingRepository(
                provider = instance(tag = "default"),
                otherProvider = instance(tag = "other"),
                json = instance(),
            )
        }
        bindSingleton<UserRepository> {
            DefaultUserRepository(
                provider = instance(tag = "default"),
                otherProvider = instance(tag = "other"),
            )
        }
        bindSingleton<UserRateLimitRepository> {
            DefaultUserRateLimitRepository(
                userRateLimitDao = instance(),
            )
        }
        bindSingleton<TranslationRepository> {
            DefaultTranslationRepository(
                provider = instance(tag = "default"),
            )
        }
        bindSingleton<FallbackTranslationRepository> {
            DefaultFallbackTranslationRepository(
                translationProviderFactory = instance(),
            )
        }
        bindSingleton<FollowedHashtagCache> {
            DefaultFollowedHashtagCache(
                tagRepository = instance(),
            )
        }
        bindSingleton<AttachmentCache> {
            DefaultAttachmentCache()
        }
    }
