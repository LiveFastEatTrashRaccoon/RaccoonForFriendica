package com.livefast.eattrash.raccoonforfriendica.main

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel

interface RootMvi : Mvi<RootMvi.Intent, RootMvi.UiState, RootMvi.Effect> {
    sealed interface Intent

    data class UiState(val currentSettings: SettingsModel? = null)

    sealed interface Effect {
        data object InitializationFinished : Effect
    }
}
