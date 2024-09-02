package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di

import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import org.koin.dsl.module

val featureNodeInfoModule =
    module {
        factory<NodeInfoMviModel> {
            NodeInfoViewModel(
                nodeInfoRepository = get(),
            )
        }
    }
