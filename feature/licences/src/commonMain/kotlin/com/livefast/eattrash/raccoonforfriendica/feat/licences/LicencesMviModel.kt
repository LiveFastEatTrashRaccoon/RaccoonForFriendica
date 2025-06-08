package com.livefast.eattrash.raccoonforfriendica.feat.licences

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.feat.licences.models.LicenceItem

interface LicencesMviModel :
    ScreenModel,
    MviModel<LicencesMviModel.Intent, LicencesMviModel.State, LicencesMviModel.Effect> {
    sealed interface Intent

    data class State(val items: List<LicenceItem> = emptyList(), val hideNavigationBarWhileScrolling: Boolean = true)

    sealed interface Effect
}
