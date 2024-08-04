package com.github.akesiseli.raccoonforfriendica.domain.content.repository.di

import com.github.akesiseli.raccoonforfriendica.domain.content.repository.DefaultTimelineRepository
import com.github.akesiseli.raccoonforfriendica.domain.content.repository.TimelineRepository
import org.koin.dsl.module

val domainContentRepositoryModule =
    module {
        single<TimelineRepository> {
            DefaultTimelineRepository(
                provider = get(),
            )
        }
    }
