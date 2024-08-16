package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.feature.login.LoginMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.LoginViewModel
import org.koin.dsl.module

val featureLoginModule =
    module {
        factory<LoginMviModel> {
            LoginViewModel(
                credentialsRepository = get(),
                apiConfigurationRepository = get(),
                loginUseCase = get(),
            )
        }
    }
