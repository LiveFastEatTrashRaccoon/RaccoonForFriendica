package com.livefast.eattrash.raccoonforfriendica.feature.profile.di

import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileViewModel
import org.koin.dsl.module

val featureProfileModule =
    module {
        factory<ProfileMviModel> {
            ProfileViewModel(
                apiConfigurationRepository = get(),
                logoutUseCase = get(),
            )
        }
    }
