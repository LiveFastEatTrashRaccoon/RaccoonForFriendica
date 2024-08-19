package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginViewModel
import org.koin.dsl.module

val featureLoginModule =
    module {
        factory<LegacyLoginMviModel> {
            LegacyLoginViewModel(
                credentialsRepository = get(),
                apiConfigurationRepository = get(),
                loginUseCase = get(),
            )
        }
    }
