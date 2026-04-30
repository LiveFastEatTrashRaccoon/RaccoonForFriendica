package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.AlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultAlbumPhotoPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultDirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultEventPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultFollowRequestPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultFollowedHashtagsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultNotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultSearchPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultTimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultTimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultUnpublishedPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultUserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DirectMessagesPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.EventPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowRequestPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.FollowedHashtagsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.SearchPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelineNavigationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UnpublishedPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val contentPaginationModule =
    DI.Module("ContentPaginationModule") {
        bindProvider<AlbumPhotoPaginationManager> {
            DefaultAlbumPhotoPaginationManager(
                albumRepository = instance(),
            )
        }
        bindProvider<DirectMessagesPaginationManager> {
            DefaultDirectMessagesPaginationManager(
                directMessageRepository = instance(),
                emojiHelper = instance(),
            )
        }
        bindProvider<EventPaginationManager> {
            DefaultEventPaginationManager(
                eventRepository = instance(),
            )
        }
        bindProvider<ExplorePaginationManager> {
            DefaultExplorePaginationManager(
                trendingRepository = instance(),
                userRepository = instance(),
                emojiHelper = instance(),
                replyHelper = instance(),
                accountRepository = instance(),
                stopWordRepository = instance(),
                notificationCenter = instance(),
            )
        }
        bindProvider<FollowedHashtagsPaginationManager> {
            DefaultFollowedHashtagsPaginationManager(
                tagRepository = instance(),
            )
        }
        bindProvider<FollowRequestPaginationManager> {
            DefaultFollowRequestPaginationManager(
                userRepository = instance(),
                emojiHelper = instance(),
            )
        }
        bindProvider<NotificationsPaginationManager> {
            DefaultNotificationsPaginationManager(
                notificationRepository = instance(),
                userRepository = instance(),
                emojiHelper = instance(),
                replyHelper = instance(),
            )
        }
        bindProvider<SearchPaginationManager> {
            DefaultSearchPaginationManager(
                searchRepository = instance(),
                userRepository = instance(),
                emojiHelper = instance(),
                replyHelper = instance(),
                accountRepository = instance(),
                stopWordRepository = instance(),
                notificationCenter = instance(),
            )
        }
        bindProvider<TimelinePaginationManager> {
            DefaultTimelinePaginationManager(
                timelineRepository = instance(),
                timelineEntryRepository = instance(),
                accountRepository = instance(),
                userRateLimitRepository = instance(),
                emojiHelper = instance(),
                replyHelper = instance(),
                stopWordRepository = instance(),
                followedHashtagCache = instance(),
                notificationCenter = instance(),
            )
        }
        bindProvider<UnpublishedPaginationManager> {
            DefaultUnpublishedPaginationManager(
                scheduledEntryRepository = instance(),
                draftRepository = instance(),
                notificationCenter = instance(),
            )
        }
        bindProvider<UserPaginationManager> {
            DefaultUserPaginationManager(
                userRepository = instance(),
                timelineEntryRepository = instance(),
                circlesRepository = instance(),
                accountRepository = instance(),
                userRateLimitRepository = instance(),
                emojiHelper = instance(),
                notificationCenter = instance(),
            )
        }
        bindSingleton<TimelineNavigationManager> {
            DefaultTimelineNavigationManager(
                paginationManager = instance(),
            )
        }
    }
