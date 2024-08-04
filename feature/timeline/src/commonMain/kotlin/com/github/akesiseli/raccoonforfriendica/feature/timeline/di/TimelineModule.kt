package com.github.akesiseli.raccoonforfriendica.feature.timeline.di

import com.github.akesiseli.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.github.akesiseli.raccoonforfriendica.feature.timeline.TimelineViewModel
import org.koin.dsl.module

val featureTimelineModule =
    module {
        factory<TimelineMviModel> {
            TimelineViewModel(
                paginationManager = get(),
                apiConfigurationRepository = get(),
            )
        }
    }
