package com.livefast.eattrash.raccoonforfriendica.main

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel

interface RootMviModel : MviModel<RootMviModel.Intent, RootMviModel.UiState, RootMviModel.Effect> {
    sealed interface Intent

    data class UiState(val currentSettings: SettingsModel? = null)

    sealed interface Effect {
        data object InitializationFinished : Effect
    }
}
