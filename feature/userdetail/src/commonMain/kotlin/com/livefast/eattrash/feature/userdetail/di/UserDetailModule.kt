package com.livefast.eattrash.feature.userdetail.di

import com.livefast.eattrash.feature.userdetail.classic.UserDetailMviModel
import com.livefast.eattrash.feature.userdetail.classic.UserDetailViewModel
import com.livefast.eattrash.feature.userdetail.forum.ForumListMviModel
import com.livefast.eattrash.feature.userdetail.forum.ForumListViewModel
import org.koin.dsl.module

val featureUserDetailModule =
    module {
        factory<UserDetailMviModel> { params ->
            UserDetailViewModel(
                id = params[0],
                userRepository = get(),
                paginationManager = get(),
                timelineEntryRepository = get(),
                identityRepository = get(),
                settingsRepository = get(),
                hapticFeedback = get(),
                userCache = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
            )
        }
        factory<ForumListMviModel> { params ->
            ForumListViewModel(
                id = params[0],
                userRepository = get(),
                paginationManager = get(),
                timelineEntryRepository = get(),
                identityRepository = get(),
                settingsRepository = get(),
                hapticFeedback = get(),
                userCache = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
            )
        }
    }
