package com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.main

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.models.AcknowledgementModel

interface AcknowledgementsMvi : Mvi<AcknowledgementsMvi.Intent, AcknowledgementsMvi.State, AcknowledgementsMvi.Effect> {
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
