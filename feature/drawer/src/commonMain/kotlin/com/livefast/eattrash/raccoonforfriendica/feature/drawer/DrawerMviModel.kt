package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Stable
interface DrawerMviModel :
    ScreenModel,
    MviModel<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect> {
    sealed interface Intent

    data class State(
        val user: UserModel? = null,
        val node: String? = null,
        val hasDirectMessages: Boolean = false,
        val hasGallery: Boolean = false,
    )

    sealed interface Effect
}
