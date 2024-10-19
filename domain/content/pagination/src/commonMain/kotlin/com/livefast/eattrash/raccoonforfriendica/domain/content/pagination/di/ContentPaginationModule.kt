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
import org.koin.dsl.module

val domainContentPaginationModule =
    module {
        factory<TimelinePaginationManager> {
            DefaultTimelinePaginationManager(
                timelineRepository = get(),
                timelineEntryRepository = get(),
                emojiHelper = get(),
                replyHelper = get(),
                notificationCenter = get(),
            )
        }
        factory<NotificationsPaginationManager> {
            DefaultNotificationsPaginationManager(
                notificationRepository = get(),
                emojiHelper = get(),
                replyHelper = get(),
                userRepository = get(),
            )
        }
        factory<ExplorePaginationManager> {
            DefaultExplorePaginationManager(
                trendingRepository = get(),
                userRepository = get(),
                emojiHelper = get(),
                replyHelper = get(),
                notificationCenter = get(),
            )
        }
        factory<UserPaginationManager> {
            DefaultUserPaginationManager(
                userRepository = get(),
                timelineEntryRepository = get(),
                circlesRepository = get(),
                emojiHelper = get(),
                notificationCenter = get(),
            )
        }
        factory<FavoritesPaginationManager> {
            DefaultFavoritesPaginationManager(
                timelineEntryRepository = get(),
                emojiHelper = get(),
                replyHelper = get(),
                notificationCenter = get(),
            )
        }
        factory<FollowedHashtagsPaginationManager> {
            DefaultFollowedHashtagsPaginationManager(
                tagRepository = get(),
            )
        }
        factory<SearchPaginationManager> {
            DefaultSearchPaginationManager(
                searchRepository = get(),
                userRepository = get(),
                emojiHelper = get(),
                replyHelper = get(),
                notificationCenter = get(),
            )
        }
        factory<FollowRequestPaginationManager> {
            DefaultFollowRequestPaginationManager(
                userRepository = get(),
                emojiHelper = get(),
            )
        }
        factory<DirectMessagesPaginationManager> {
            DefaultDirectMessagesPaginationManager(
                directMessageRepository = get(),
                emojiHelper = get(),
            )
        }
        factory<AlbumPhotoPaginationManager> {
            DefaultAlbumPhotoPaginationManager(
                albumRepository = get(),
            )
        }
        factory<UnpublishedPaginationManager> {
            DefaultUnpublishedPaginationManager(
                scheduledEntryRepository = get(),
                draftRepository = get(),
                notificationCenter = get(),
            )
        }
        factory<EventPaginationManager> {
            DefaultEventPaginationManager(
                eventRepository = get(),
            )
        }
    }
