package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NodeInfoViewModel(
    private val nodeInfoRepository: NodeInfoRepository,
    private val settingsRepository: SettingsRepository,
    private val emojiHelper: EmojiHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
) : ViewModel(),
    MviModelDelegate<NodeInfoMviModel.Intent, NodeInfoMviModel.State, NodeInfoMviModel.Effect>
    by DefaultMviModelDelegate(initialState = NodeInfoMviModel.State()),
    NodeInfoMviModel {
    init {
        viewModelScope.launch {
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
