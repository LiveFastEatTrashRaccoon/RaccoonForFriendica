package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultAccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultNotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTrendsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NotificationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TrendsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainContentRepositoryModule =
    module {
        single<TimelineRepository> {
            DefaultTimelineRepository(
                provider = get(named("default")),
            )
        }
        single<AccountRepository> {
            DefaultAccountRepository(
                provider = get(named("default")),
            )
        }
        single<NotificationRepository> {
            DefaultNotificationRepository(
                provider = get(named("default")),
            )
        }
        single<TrendsRepository> {
            DefaultTrendsRepository(
                provider = get(named("default")),
            )
        }
    }
