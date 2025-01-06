package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultAlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultDirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultEventPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultFavoritesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultFollowRequestPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultFollowedHashtagsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultNotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultSearchPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultTimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultUnpublishedPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultUserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.EventPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FavoritesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowRequestPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowedHashtagsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.SearchPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UnpublishedPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val contentPaginationModule =
    DI.Module("ContentPaginationModule") {
        bind<AlbumPhotoPaginationManager> {
            provider {
                DefaultAlbumPhotoPaginationManager(
                    albumRepository = instance(),
                )
            }
        }
        bind<DirectMessagesPaginationManager> {
            provider {
                DefaultDirectMessagesPaginationManager(
                    directMessageRepository = instance(),
                    emojiHelper = instance(),
                )
            }
        }
        bind<EventPaginationManager> {
            provider {
                DefaultEventPaginationManager(
                    eventRepository = instance(),
                )
            }
        }
        bind<ExplorePaginationManager> {
            provider {
                DefaultExplorePaginationManager(
                    trendingRepository = instance(),
                    userRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<FavoritesPaginationManager> {
            provider {
                DefaultFavoritesPaginationManager(
                    timelineEntryRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<FollowedHashtagsPaginationManager> {
            provider {
                DefaultFollowedHashtagsPaginationManager(
                    tagRepository = instance(),
                )
            }
        }
        bind<FollowRequestPaginationManager> {
            provider {
                DefaultFollowRequestPaginationManager(
                    userRepository = instance(),
                    emojiHelper = instance(),
                )
            }
        }
        bind<NotificationsPaginationManager> {
            provider {
                DefaultNotificationsPaginationManager(
                    notificationRepository = instance(),
                    userRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                )
            }
        }
        bind<SearchPaginationManager> {
            provider {
                DefaultSearchPaginationManager(
                    searchRepository = instance(),
                    userRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<TimelinePaginationManager> {
            provider {
                DefaultTimelinePaginationManager(
                    timelineRepository = instance(),
                    timelineEntryRepository = instance(),
                    accountRepository = instance(),
                    userRateLimitRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<UnpublishedPaginationManager> {
            provider {
                DefaultUnpublishedPaginationManager(
                    scheduledEntryRepository = instance(),
                    draftRepository = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<UserPaginationManager> {
            provider {
                DefaultUserPaginationManager(
                    userRepository = instance(),
                    timelineEntryRepository = instance(),
                    circlesRepository = instance(),
                    emojiHelper = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
