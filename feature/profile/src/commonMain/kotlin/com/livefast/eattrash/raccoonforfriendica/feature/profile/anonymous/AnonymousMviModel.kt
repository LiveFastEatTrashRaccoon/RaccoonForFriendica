package com.livefast.eattrash.raccoonforfriendica.feature.profile.anonymous

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel

interface AnonymousMviModel :
    ScreenModel,
    MviModel<AnonymousMviModel.Intent, AnonymousMviModel.State, AnonymousMviModel.Effect> {
    sealed interface Intent {
        data object StartOauth2Flow : Intent

        data object StartLegacyFlow : Intent
    }

    object State

    sealed interface Effect {
        data class Failure(
            val message: String?,
        ) : Effect
    }
}
