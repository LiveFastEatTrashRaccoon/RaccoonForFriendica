package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous.AnonymousMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous.AnonymousViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.domain.DefaultMyAccountCache
import com.livefast.eattrash.raccoonforfriendica.feature.profile.domain.MyAccountCache
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileViewModel
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
                myAccountCache = get(),
                authManager = get(),
            )
        }
        factory<MyAccountMviModel> {
            MyAccountViewModel(
                accountRepository = get(),
                userRepository = get(),
                paginationManager = get(),
                timelineEntryRepository = get(),
                myAccountCache = get(),
                settingsRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                imagePreloadManager = get(),
            )
        }
        factory<AnonymousMviModel> {
            AnonymousViewModel(
                authManager = get(),
            )
        }
        single<MyAccountCache> {
            DefaultMyAccountCache()
        }
        factory<EditProfileMviModel> {
            EditProfileViewModel(
                userRepository = get(),
            )
        }
    }
