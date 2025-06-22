package com.livefast.eattrash.raccoonforfriendica.feat.licences

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.feat.licences.models.LicenceItem

interface LicencesMviModel : MviModel<LicencesMviModel.Intent, LicencesMviModel.State, LicencesMviModel.Effect> {
    sealed interface Intent

    data class State(val items: List<LicenceItem> = emptyList(), val hideNavigationBarWhileScrolling: Boolean = true)

    sealed interface Effect
}
