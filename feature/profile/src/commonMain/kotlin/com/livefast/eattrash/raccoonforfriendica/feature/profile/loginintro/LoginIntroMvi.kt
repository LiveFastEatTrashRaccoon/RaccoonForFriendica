package com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.LoginType

interface LoginIntroMvi : Mvi<LoginIntroMvi.Intent, LoginIntroMvi.State, LoginIntroMvi.Effect> {
    sealed interface Intent {
        data class StartOauth2Flow(val type: LoginType) : Intent

        data object StartLegacyFlow : Intent
    }

    object State

    sealed interface Effect
}
