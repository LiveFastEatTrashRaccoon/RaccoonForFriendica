package com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous

import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AuthManager

class AnonymousViewModel(
    private val authManager: AuthManager,
) : DefaultMviModel<AnonymousMviModel.Intent, AnonymousMviModel.State, AnonymousMviModel.Effect>(
        initialState = AnonymousMviModel.State,
    ),
    AnonymousMviModel {
    override fun reduce(intent: AnonymousMviModel.Intent) {
        when (intent) {
            AnonymousMviModel.Intent.StartOauth2Flow -> authManager.openLogin()
            AnonymousMviModel.Intent.StartLegacyFlow -> authManager.openLegacyLogin()
        }
    }
}
