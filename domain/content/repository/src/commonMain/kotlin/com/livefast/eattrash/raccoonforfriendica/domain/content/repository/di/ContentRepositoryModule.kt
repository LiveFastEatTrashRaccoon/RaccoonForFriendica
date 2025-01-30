package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AnnouncementsManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.CirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAnnouncementRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAnnouncementsManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultCirclesRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultDirectMessageRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultDraftRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEmojiRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultEventRepository
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
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal val cacheModule =
    DI.Module("ContentRepositoryCacheModule") {
        bind<LocalItemCache<UserModel>> { singleton { DefaultLocalItemCache() } }
        bind<LocalItemCache<TimelineEntryModel>> { singleton { DefaultLocalItemCache() } }
        bind<LocalItemCache<EventModel>> { singleton { DefaultLocalItemCache() } }
        bind<LocalItemCache<CircleModel>> { singleton { DefaultLocalItemCache() } }
        bind<LocalItemCache<ScheduledEntryRepository>> { singleton { DefaultLocalItemCache() } }
    }

val contentRepositoryModule =
    DI.Module("ContentRepositoryModule") {
        import(cacheModule)

        bind<AnnouncementRepository> {
            singleton {
                DefaultAnnouncementRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<AnnouncementsManager> {
            singleton {
                DefaultAnnouncementsManager(
                    announcementRepository = instance(),
                )
            }
        }
        bind<CirclesRepository> {
            singleton {
                DefaultCirclesRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<DirectMessageRepository> {
            singleton {
                DefaultDirectMessageRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<DraftRepository> {
            singleton {
                DefaultDraftRepository(
                    draftDao = instance(),
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<EmojiHelper> {
            singleton {
                DefaultEmojiHelper(
                    repository = instance(),
                )
            }
        }
        bind<EmojiRepository> {
            singleton {
                DefaultEmojiRepository(
                    provider = instance(tag = "default"),
                    otherProvider = instance(tag = "other"),
                )
            }
        }
        bind<EventRepository> {
            singleton {
                DefaultEventRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<InboxManager> {
            singleton {
                DefaultInboxManager(
                    notificationRepository = instance(),
                    markerRepository = instance(),
                )
            }
        }
        bind<MarkerRepository> {
            singleton {
                DefaultMarkerRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<MediaRepository> {
            singleton {
                DefaultMediaRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<NodeInfoRepository> {
            singleton {
                DefaultNodeInfoRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<NotificationRepository> {
            singleton {
                DefaultNotificationRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<PhotoAlbumRepository> {
            singleton {
                DefaultPhotoAlbumRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<PhotoRepository> {
            singleton {
                DefaultPhotoRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<PushNotificationRepository> {
            singleton {
                DefaultPushNotificationRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<ReplyHelper> {
            singleton {
                DefaultReplyHelper(
                    entryRepository = instance(),
                )
            }
        }
        bind<ReportRepository> {
            singleton {
                DefaultReportRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<ScheduledEntryRepository> {
            singleton {
                DefaultScheduledEntryRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<SearchRepository> {
            singleton {
                DefaultSearchRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<SupportedFeatureRepository> {
            singleton {
                DefaultSupportedFeatureRepository(
                    nodeInfoRepository = instance(),
                )
            }
        }
        bind<TagRepository> {
            singleton {
                DefaultTagRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<TimelineEntryRepository> {
            singleton {
                DefaultTimelineEntryRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<TimelineRepository> {
            singleton {
                DefaultTimelineRepository(
                    provider = instance(tag = "default"),
                    otherProvider = instance(tag = "other"),
                )
            }
        }
        bind<TrendingRepository> {
            singleton {
                DefaultTrendingRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<UserRepository> {
            singleton {
                DefaultUserRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<UserRateLimitRepository> {
            singleton {
                DefaultUserRateLimitRepository(
                    userRateLimitDao = instance(),
                )
            }
        }
        bind<TranslationRepository>(tag = "default") {
            singleton {
                DefaultTranslationRepository(
                    provider = instance(tag = "default"),
                )
            }
        }
        bind<TranslationRepository>(tag = "fallback") {
            singleton {
                FallbackTranslationRepository(
                    service = instance(),
                )
            }
        }
    }
