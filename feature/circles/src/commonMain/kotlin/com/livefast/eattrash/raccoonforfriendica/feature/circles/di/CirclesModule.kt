package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.manage.ManageUserCirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class CircleMembersViewModelArgs(val id: String) : ViewModelCreationArgs

data class CircleTimelineViewModelArgs(val id: String) : ViewModelCreationArgs

data class ManageUserCirclesViewModelArgs(val userId: String) : ViewModelCreationArgs

val circlesModule =
    DI.Module("CirclesModule") {
        bindViewModelWithArgs { args: CircleMembersViewModelArgs ->
            CircleMembersViewModel(
                id = args.id,
                paginationManager = instance(),
                circlesRepository = instance(),
                settingsRepository = instance(),
                searchPaginationManager = instance(),
                imagePreloadManager = instance(),
                imageAutoloadObserver = instance(),
            )
        }
        bindViewModel {
            CirclesViewModel(
                circlesRepository = instance(),
                settingsRepository = instance(),
                userRepository = instance(),
            )
        }
        bindViewModelWithArgs { args: CircleTimelineViewModelArgs ->
            CircleTimelineViewModel(
                id = args.id,
                paginationManager = instance(),
                identityRepository = instance(),
                timelineEntryRepository = instance(),
                settingsRepository = instance(),
                userRepository = instance(),
                apiConfigurationRepository = instance(),
                accountRepository = instance(),
                instanceShortcutRepository = instance(),
                circleCache = instance(),
                hapticFeedback = instance(),
                imagePreloadManager = instance(),
                blurHashRepository = instance(),
                imageAutoloadObserver = instance(),
                toggleEntryFavorite = instance(),
                toggleEntryDislike = instance(),
                getTranslation = instance(),
                getInnerUrl = instance(),
                timelineNavigationManager = instance(),
                notificationCenter = instance(),
            )
        }
        bindViewModelWithArgs { args: ManageUserCirclesViewModelArgs ->
            ManageUserCirclesViewModel(
                userId = args.userId,
                circlesRepository = instance(),
                userCache = instance(),
                userRepository = instance(),
            )
        }
    }
