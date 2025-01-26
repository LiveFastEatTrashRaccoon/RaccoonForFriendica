package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val circlesModule =
    DI.Module("CirclesModule") {
        bind<CircleMembersMviModel> {
            factory { id: String ->
                CircleMembersViewModel(
                    id = id,
                    paginationManager = instance(),
                    circlesRepository = instance(),
                    settingsRepository = instance(),
                    searchPaginationManager = instance(),
                    imagePreloadManager = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
        bind<CirclesMviModel> {
            provider {
                CirclesViewModel(
                    circlesRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                )
            }
        }
        bind<CircleTimelineMviModel> {
            factory { id: String ->
                CircleTimelineViewModel(
                    id = id,
                    paginationManager = instance(),
                    identityRepository = instance(),
                    timelineEntryRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                    circleCache = instance(),
                    hapticFeedback = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    getTranslation = instance(),
                    timelineNavigationManager = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
