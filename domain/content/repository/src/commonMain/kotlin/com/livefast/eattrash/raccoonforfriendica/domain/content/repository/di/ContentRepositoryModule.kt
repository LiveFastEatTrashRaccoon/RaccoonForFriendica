package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository
import org.koin.dsl.module

val domainContentRepositoryModule =
    module {
        single<TimelineRepository> {
            DefaultTimelineRepository(
                provider = get(),
            )
        }
    }
