package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val loginModule =
    DI.Module("LoginModule") {
        bind<LegacyLoginMviModel> {
            provider {
                LegacyLoginViewModel(
                    credentialsRepository = instance(),
                    apiConfigurationRepository = instance(),
                    loginUseCase = instance(),
                )
            }
        }
        bind<LoginMviModel> {
            factory { type: LoginType ->
                LoginViewModel(
                    type = type,
                    apiConfigurationRepository = instance(),
                    credentialsRepository = instance(),
                    authManager = instance(),
                    loginUseCase = instance(),
                )
            }
        }
    }
