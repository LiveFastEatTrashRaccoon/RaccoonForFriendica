package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory(binds = [NodeInfoMviModel::class])
class NodeInfoViewModel(
    private val nodeInfoRepository: NodeInfoRepository,
    private val settingsRepository: SettingsRepository,
    private val emojiHelper: EmojiHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : DefaultMviModel<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect>(
        initialState = NodeInfoMviModel.State(),
    ),
    NodeInfoMviModel {
    init {
        screenModelScope.launch {
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)

            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                                settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

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
