package com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro

import androidx.lifecycle.ViewModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class LoginIntroViewModel(private val authManager: AuthManager) :
    ViewModel(),
    MviDelegate<LoginIntroMvi.Intent, LoginIntroMvi.State, LoginIntroMvi.Effect>
    by DefaultMviDelegate(initialState = LoginIntroMvi.State),
    LoginIntroMvi {
    override fun reduce(intent: LoginIntroMvi.Intent) {
        when (intent) {
            is LoginIntroMvi.Intent.StartOauth2Flow -> authManager.openLogin(intent.type)
            LoginIntroMvi.Intent.StartLegacyFlow -> authManager.openLegacyLogin()
        }
    }
}
