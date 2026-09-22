package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel

@Stable
interface ProfileMviModel :
    MviModel<ProfileMviModel.Intent, ProfileMviModel.State, ProfileMviModel.Effect> {
    sealed interface Intent {
        data object Logout : Intent
    }

    data class State(
        val currentUserId: String? = null,
        val loading: Boolean = false,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect
}
