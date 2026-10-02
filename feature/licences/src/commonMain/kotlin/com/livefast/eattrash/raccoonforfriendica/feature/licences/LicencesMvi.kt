package com.livefast.eattrash.raccoonforfriendica.feature.licences

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.feature.licences.models.LicenceItem

interface LicencesMvi : Mvi<LicencesMvi.Intent, LicencesMvi.State, LicencesMvi.Effect> {
    sealed interface Intent

    data class State(val items: List<LicenceItem> = emptyList(), val hideNavigationBarWhileScrolling: Boolean = true)

    sealed interface Effect
}
