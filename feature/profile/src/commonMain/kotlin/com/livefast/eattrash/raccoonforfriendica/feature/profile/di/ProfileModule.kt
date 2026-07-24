package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel {
        EditProfileViewModel(
            userRepository = get(),
            emojiRepository = get(),
            settingsRepository = get(),
            apiConfigurationRepository = get(),
            supportedFeatureRepository = get(),
            imageAutoloadObserver = get(),
        )
    }
    viewModel {
        LoginIntroViewModel(
            authManager = get(),
        )
    }
    viewModel {
        MyAccountViewModel(
            userRepository = get(),
            identityRepository = get(),
            paginationManager = get(),
            timelineEntryRepository = get(),
            settingsRepository = get(),
            hapticFeedback = get(),
            imagePreloadManager = get(),
            blurHashRepository = get(),
            emojiHelper = get(),
            replyHelper = get(),
            imageAutoloadObserver = get(),
            logout = get(),
            toggleEntryFavorite = get(),
            toggleEntryDislike = get(),
            getInnerUrl = get(),
            notificationCenter = get(),
        )
    }
    viewModel {
        ProfileViewModel(
            identityRepository = get(),
            accountRepository = get(),
            settingsRepository = get(),
            logoutUseCase = get(),
            switchAccountUseCase = get(),
            deleteAccountUseCase = get(),
            authManager = get(),
            imageAutoloadObserver = get(),
        )
    }
}
