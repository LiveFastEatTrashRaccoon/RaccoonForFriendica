package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel

@Stable
interface NodeInfoMviModel : MviModel<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect> {
    sealed interface Intent

    data class State(
        val info: NodeInfoModel? = null,
        val autoloadImages: Boolean = true,
        val hideNavigationBarWhileScrolling: Boolean = true,
    )

    sealed interface Effect
}
