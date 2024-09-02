package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import kotlinx.coroutines.launch

class NodeInfoViewModel(
    private val nodeInfoRepository: NodeInfoRepository,
) : DefaultMviModel<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect>(
        initialState = NodeInfoMviModel.State(),
    ),
    NodeInfoMviModel {
    init {
        screenModelScope.launch {
            val info = nodeInfoRepository.getInfo()
            updateState { it.copy(info = info) }
        }
    }
}
