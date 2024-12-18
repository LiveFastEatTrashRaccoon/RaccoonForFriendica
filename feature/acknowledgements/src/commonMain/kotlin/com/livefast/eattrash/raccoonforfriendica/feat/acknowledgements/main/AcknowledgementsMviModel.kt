package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.models.AcknowledgementModel

interface AcknowledgementsMviModel :
    ScreenModel,
    MviModel<AcknowledgementsMviModel.Intent, AcknowledgementsMviModel.State, AcknowledgementsMviModel.Effect> {
    sealed interface Intent {
        data object Refresh : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val items: List<AcknowledgementModel> = emptyList(),
    )

    sealed interface Effect
}
