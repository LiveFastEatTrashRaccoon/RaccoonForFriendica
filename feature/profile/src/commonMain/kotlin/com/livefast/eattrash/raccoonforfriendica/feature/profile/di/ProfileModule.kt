package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountViewModel
import org.koin.dsl.module

val featureProfileModule =
    module {
        factory<ProfileMviModel> {
            ProfileViewModel(
                apiConfigurationRepository = get(),
                logoutUseCase = get(),
            )
        }
        factory<MyAccountMviModel> {
            MyAccountViewModel(
                accountRepository = get(),
                userRepository = get(),
                paginationManager = get(),
                timelineEntryRepository = get(),
            )
        }
        single<MyAccountCache> {
            DefaultMyAccountCache()
        }
    }
