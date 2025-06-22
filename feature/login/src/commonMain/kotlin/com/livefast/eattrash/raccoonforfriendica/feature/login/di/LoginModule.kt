package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginViewModel
import org.kodein.di.DI
import org.kodein.di.instance

data class LoginViewModelArgs(val type: LoginType) : ViewModelCreationArgs

val loginModule =
    DI.Module("LoginModule") {
        bindViewModel {
            LegacyLoginViewModel(
                credentialsRepository = instance(),
                apiConfigurationRepository = instance(),
                loginUseCase = instance(),
            )
        }
        bindViewModelWithArgs { args: LoginViewModelArgs ->
            LoginViewModel(
                type = args.type,
                apiConfigurationRepository = instance(),
                credentialsRepository = instance(),
                authManager = instance(),
                loginUseCase = instance(),
            )
        }
    }
