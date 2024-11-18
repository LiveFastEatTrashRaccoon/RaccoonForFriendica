package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineViewModel
import org.koin.dsl.module

val featureCirclesModule =
    module {
        factory<CirclesMviModel> {
            CirclesViewModel(
                circlesRepository = get(),
                settingsRepository = get(),
                userRepository = get(),
            )
        }
        factory<CircleMembersMviModel> { params ->
            CircleMembersViewModel(
                id = params[0],
                paginationManager = get(),
                circlesRepository = get(),
                settingsRepository = get(),
                searchPaginationManager = get(),
                imagePreloadManager = get(),
                imageAutoloadObserver = get(),
            )
        }
        factory<CircleTimelineMviModel> { param ->
            CircleTimelineViewModel(
                id = param[0],
                paginationManager = get(),
                identityRepository = get(),
                timelineEntryRepository = get(),
                settingsRepository = get(),
                circleCache = get(),
                userRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
                blurHashRepository = get(),
                imageAutoloadObserver = get(),
            )
        }
    }
