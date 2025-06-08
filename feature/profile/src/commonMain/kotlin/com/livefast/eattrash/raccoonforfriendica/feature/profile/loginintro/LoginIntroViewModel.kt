package com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro

import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager

class LoginIntroViewModel(private val authManager: AuthManager) :
    DefaultMviModel<LoginIntroMviModel.Intent, LoginIntroMviModel.State, LoginIntroMviModel.Effect>(
        initialState = LoginIntroMviModel.State,
    ),
    LoginIntroMviModel {
    override fun reduce(intent: LoginIntroMviModel.Intent) {
        when (intent) {
            is LoginIntroMviModel.Intent.StartOauth2Flow -> authManager.openLogin(intent.type)
            LoginIntroMviModel.Intent.StartLegacyFlow -> authManager.openLegacyLogin()
        }
    }
}
