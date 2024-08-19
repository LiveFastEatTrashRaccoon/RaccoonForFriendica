package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginViewModel
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
        factory<LoginMviModel> {
            LoginViewModel(
                credentialsRepository = get(),
                apiConfigurationRepository = get(),
                authManager = get(),
                loginUseCase = get(),
            )
        }
    }
