package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError

@Stable
interface PermanentDrawerMviModel :
    MviModel<PermanentDrawerMviModel.Intent, PermanentDrawerMviModel.State, PermanentDrawerMviModel.Effect> {
    sealed interface Intent {
        data object ToggleExpanded : Intent
    }

    data class State(
        val isLogged: Boolean = false,
        val unreadItems: Int = 0,
        val hasDirectMessages: Boolean = false,
        val hasGallery: Boolean = false,
        val hasCalendar: Boolean = false,
        val hasAnnouncements: Boolean = false,
        val anonymousChangeNodeName: String = "",
        val anonymousChangeNodeValidationInProgress: Boolean = false,
        val anonymousChangeNodeNameError: ValidationError? = null,
        val isExpanded: Boolean = true,
    )

    sealed interface Effect
}
