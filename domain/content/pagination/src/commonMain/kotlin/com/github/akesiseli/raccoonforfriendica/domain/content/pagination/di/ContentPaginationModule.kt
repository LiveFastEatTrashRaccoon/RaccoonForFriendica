package com.github.akesiseli.raccoonforfriendica.domain.content.pagination.di

import com.github.akesiseli.raccoonforfriendica.domain.content.pagination.DefaultTimelinePaginationManager
import com.github.akesiseli.raccoonforfriendica.domain.content.pagination.TimelinePaginationManager
import org.koin.dsl.module

val domainContentPaginationModule =
    module {
        single<TimelinePaginationManager> {
            DefaultTimelinePaginationManager(
                timelineRepository = get(),
            )
        }
    }
