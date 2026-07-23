package com.livefast.eattrash.raccoonforfriendica.feature.login.di

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class LoginViewModelArgs(val type: LoginType)

val loginModule = module {
    viewModel {
        LegacyLoginViewModel(
            credentialsRepository = get(),
            apiConfigurationRepository = get(),
            loginUseCase = get(),
        )
    }
    viewModel { params ->
        val args: LoginViewModelArgs = params.get()
        LoginViewModel(
            type = args.type,
            apiConfigurationRepository = get(),
            credentialsRepository = get(),
            authManager = get(),
            loginUseCase = get(),
        )
    }
}
