package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val profileModule =
    DI.Module("ProfileModule") {
        bind<EditProfileMviModel> {
            provider {
                EditProfileViewModel(
                    userRepository = instance(),
                    emojiRepository = instance(),
                    settingsRepository = instance(),
                    apiConfigurationRepository = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
        bind<LoginIntroMviModel> {
            provider {
                LoginIntroViewModel(
                    authManager = instance(),
                )
            }
        }
        bind<MyAccountMviModel> {
            provider {
                MyAccountViewModel(
                    userRepository = instance(),
                    identityRepository = instance(),
                    paginationManager = instance(),
                    timelineEntryRepository = instance(),
                    settingsRepository = instance(),
                    hapticFeedback = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    imageAutoloadObserver = instance(),
                    logout = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    getInnerUrl = instance(),
                    notificationCenter = instance(),
                )
            }
        }
        bind<ProfileMviModel> {
            provider {
                ProfileViewModel(
                    identityRepository = instance(),
                    accountRepository = instance(),
                    settingsRepository = instance(),
                    logoutUseCase = instance(),
                    switchAccountUseCase = instance(),
                    deleteAccountUseCase = instance(),
                    authManager = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
    }
