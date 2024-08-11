package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel

@Stable
interface ProfileMviModel :
    ScreenModel,
    MviModel<ProfileMviModel.Intent, ProfileMviModel.State, ProfileMviModel.Effect> {
    sealed interface Intent {
        data object Logout : Intent
    }

    data class State(
        val isLogged: Boolean = false,
    )

    sealed interface Effect
}
