package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import kotlinx.coroutines.launch

class NodeInfoViewModel(
    private val nodeInfoRepository: NodeInfoRepository,
    private val emojiHelper: EmojiHelper,
) : DefaultMviModel<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect>(
        initialState = NodeInfoMviModel.State(),
    ),
    NodeInfoMviModel {
    init {
        screenModelScope.launch {
            val nodeInfo =
                nodeInfoRepository.getInfo()?.let { info ->
                    info.copy(
                        contact =
                            with(emojiHelper) {
                                info.contact?.withEmojisIfMissing()
                            },
                    )
                }
            updateState { it.copy(info = nodeInfo) }
        }
    }
}
