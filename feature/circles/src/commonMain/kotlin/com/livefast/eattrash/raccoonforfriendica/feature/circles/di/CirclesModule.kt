package com.livefast.eattrash.raccoonforfriendica.feature.circles.di

import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.manage.ManageUserCirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class CircleMembersViewModelArgs(val id: String)

data class CircleTimelineViewModelArgs(val id: String)

data class ManageUserCirclesViewModelArgs(val userId: String)

val circlesModule = module {
    viewModel { params ->
        val args: CircleMembersViewModelArgs = params.get()
        CircleMembersViewModel(
            id = args.id,
            paginationManager = get(),
            circlesRepository = get(),
            settingsRepository = get(),
            searchPaginationManager = get(),
            imagePreloadManager = get(),
            imageAutoloadObserver = get(),
        )
    }
    viewModel {
        CirclesViewModel(
            circlesRepository = get(),
            settingsRepository = get(),
            userRepository = get(),
        )
    }
    viewModel { params ->
        val args: CircleTimelineViewModelArgs = params.get()
        CircleTimelineViewModel(
            id = args.id,
            paginationManager = get(),
            identityRepository = get(),
            timelineEntryRepository = get(),
            settingsRepository = get(),
            userRepository = get(),
            apiConfigurationRepository = get(),
            accountRepository = get(),
            instanceShortcutRepository = get(),
            circleCache = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            imageAutoloadObserver = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getTranslation = get(),
            getInnerUrl = get(),
            timelineNavigationManager = get(),
            notificationCenter = get(),
        )
    }
    viewModel { params ->
        val args: ManageUserCirclesViewModelArgs = params.get()
        ManageUserCirclesViewModel(
            userId = args.userId,
            circlesRepository = get(),
            userCache = get(),
            userRepository = get(),
        )
    }
}
