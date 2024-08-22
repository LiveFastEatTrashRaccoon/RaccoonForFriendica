package com.livefast.eattrash.raccoonforfriendica.feature.timeline.di

import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineViewModel
import org.koin.dsl.module

val featureTimelineModule =
    module {
        factory<TimelineMviModel> {
            TimelineViewModel(
                paginationManager = get(),
                identityRepository = get(),
                timelineEntryRepository = get(),
                settingsRepository = get(),
                userRepository = get(),
                circlesRepository = get(),
                hapticFeedback = get(),
            )
        }
    }
