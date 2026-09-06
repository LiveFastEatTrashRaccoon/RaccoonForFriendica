package com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro

import androidx.lifecycle.ViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ContributesIntoMap(AppScope::class, binding = binding<@ViewModelKey(LoginIntroViewModel::class) ViewModel>())
@Inject
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
