package com.livefast.eattrash.raccoonforfriendica.feature.profile

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi

interface ProfileMvi : Mvi<ProfileMvi.Intent, ProfileMvi.State, ProfileMvi.Effect> {
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
