package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultNotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultTimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.ExplorePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.NotificationsPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import org.koin.dsl.module

val domainContentPaginationModule =
    module {
        factory<TimelinePaginationManager> {
            DefaultTimelinePaginationManager(
                timelineRepository = get(),
            )
        }
        factory<NotificationsPaginationManager> {
            DefaultNotificationsPaginationManager(
                notificationRepository = get(),
                accountRepository = get(),
            )
        }
        factory<ExplorePaginationManager> {
            DefaultExplorePaginationManager(
                trendsRepository = get(),
                accountRepository = get(),
            )
        }
    }
