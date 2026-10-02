package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.core.utils.validation.ValidationError
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel

interface NodeInfoMvi : Mvi<NodeInfoMvi.Intent, NodeInfoMvi.State, NodeInfoMvi.Effect> {
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
