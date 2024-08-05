package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.DefaultTimelinePaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import org.koin.dsl.module

val domainContentPaginationModule =
    module {
        single<TimelinePaginationManager> {
            DefaultTimelinePaginationManager(
                timelineRepository = get(),
            )
        }
    }
