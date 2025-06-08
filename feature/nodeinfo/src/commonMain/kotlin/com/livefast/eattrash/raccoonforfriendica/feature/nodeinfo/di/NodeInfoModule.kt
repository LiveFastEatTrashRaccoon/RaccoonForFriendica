package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di

import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val nodeInfoModule =
    DI.Module("NodeInfoModule") {
        bind<NodeInfoMviModel> {
            provider {
                NodeInfoViewModel(
                    nodeInfoRepository = instance(),
                    settingsRepository = instance(),
                    emojiHelper = instance(),
                    imageAutoloadObserver = instance(),
                )
            }
        }
    }
