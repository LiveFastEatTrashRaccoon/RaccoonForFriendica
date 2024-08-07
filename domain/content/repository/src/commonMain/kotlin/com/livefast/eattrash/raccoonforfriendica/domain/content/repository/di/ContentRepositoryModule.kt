package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import org.koin.dsl.module

val domainContentRepositoryModule =
    module {
        single<TimelineRepository> {
            DefaultTimelineRepository(
                provider = get(),
            )
        }
        single<AccountRepository> {
            DefaultAccountRepository(
                provider = get(),
            )
        }
        single<NotificationRepository> {
            DefaultNotificationRepository(
                provider = get(),
            )
        }
    }
