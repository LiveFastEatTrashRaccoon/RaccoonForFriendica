package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import org.koin.dsl.module

val featureProfileModule =
    module {
        factory<ProfileMviModel> {
            ProfileViewModel(
                identityRepository = get(),
                accountRepository = get(),
                logoutUseCase = get(),
                switchAccountUseCase = get(),
                deleteAccountUseCase = get(),
                authManager = get(),
                imageAutoloadObserver = get(),
            )
        }
        factory<MyAccountMviModel> {
            MyAccountViewModel(
                userRepository = get(),
                identityRepository = get(),
                paginationManager = get(),
                timelineEntryRepository = get(),
                settingsRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
                blurHashRepository = get(),
                emojiHelper = get(),
                replyHelper = get(),
                imageAutoloadObserver = get(),
            )
        }
        factory<LoginIntroMviModel> {
            LoginIntroViewModel(
                authManager = get(),
            )
        }
        factory<EditProfileMviModel> {
            EditProfileViewModel(
                userRepository = get(),
                emojiRepository = get(),
                imageAutoloadObserver = get(),
            )
        }
    }
