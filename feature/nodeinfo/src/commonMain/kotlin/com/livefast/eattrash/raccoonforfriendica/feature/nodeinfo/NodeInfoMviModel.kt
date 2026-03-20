package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel

@Stable
interface NodeInfoMviModel : MviModel<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect> {
    sealed interface Intent {
        data class SetAnonymousChangeNode(val nodeName: String) : Intent

        data object SubmitAnonymousChangeNode : Intent
    }

    data class State(
        val isLogged: Boolean = false,
        val info: NodeInfoModel? = null,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
        val anonymousChangeNodeName: String = "",
        val anonymousChangeNodeValidationInProgress: Boolean = false,
        val anonymousChangeNodeNameError: ValidationError? = null,
    )

    sealed interface Effect {
        data object AnonymousChangeNodeSuccess : Effect
    }
}
