package com.livefast.eattrash.feature.userdetail.di

import com.livefast.eattrash.feature.userdetail.classic.UserDetailMviModel
import com.livefast.eattrash.feature.userdetail.classic.UserDetailViewModel
import com.livefast.eattrash.feature.userdetail.forum.ForumListMviModel
import com.livefast.eattrash.feature.userdetail.forum.ForumListViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

val userDetailModule =
    DI.Module("UserDetailModule") {
        bind<UserDetailMviModel> {
            factory { id: String ->
                UserDetailViewModel(
                    id = id,
                    userRepository = instance(),
                    paginationManager = instance(),
                    timelineEntryRepository = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    hapticFeedback = instance(),
                    userCache = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    accountRepository = instance(),
                    userRateLimitRepository = instance(),
                    emojiHelper = instance(),
                    imageAutoloadObserver = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<ForumListMviModel> {
            factory { id: String ->
                ForumListViewModel(
                    id = id,
                    userRepository = instance(),
                    paginationManager = instance(),
                    timelineEntryRepository = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    hapticFeedback = instance(),
                    userCache = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    imageAutoloadObserver = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
