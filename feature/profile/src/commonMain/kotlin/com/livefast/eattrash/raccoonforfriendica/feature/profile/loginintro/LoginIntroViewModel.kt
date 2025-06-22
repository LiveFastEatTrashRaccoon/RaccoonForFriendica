package com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro

import androidx.lifecycle.ViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager

class LoginIntroViewModel(private val authManager: AuthManager) :
    ViewModel(),
    MviModelDelegate<LoginIntroMviModel.Intent, LoginIntroMviModel.State, LoginIntroMviModel.Effect>
    by DefaultMviModelDelegate(initialState = LoginIntroMviModel.State),
    LoginIntroMviModel {
    override fun reduce(intent: LoginIntroMviModel.Intent) {
        when (intent) {
            is LoginIntroMviModel.Intent.StartOauth2Flow -> authManager.openLogin(intent.type)
            LoginIntroMviModel.Intent.StartLegacyFlow -> authManager.openLegacyLogin()
        }
    }
}
