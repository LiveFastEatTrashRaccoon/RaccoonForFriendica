package com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType

interface AnonymousMviModel :
    ScreenModel,
    MviModel<AnonymousMviModel.Intent, AnonymousMviModel.State, AnonymousMviModel.Effect> {
    sealed interface Intent {
        data class StartOauth2Flow(
            val type: LoginType,
        ) : Intent

        data object StartLegacyFlow : Intent
    }

    object State

    sealed interface Effect
}
