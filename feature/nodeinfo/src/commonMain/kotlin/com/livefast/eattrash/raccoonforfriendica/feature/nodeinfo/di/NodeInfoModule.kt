package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import org.kodein.di.DI
import org.kodein.di.instance

val nodeInfoModule =
    DI.Module("NodeInfoModule") {
        bindViewModel {
            NodeInfoViewModel(
                apiConfigurationRepository = instance(),
                identityRepository = instance(),
                nodeInfoRepository = instance(),
                credentialsRepository = instance(),
                supportedFeatureRepository = instance(),
                settingsRepository = instance(),
                emojiHelper = instance(),
                imageAutoloadObserver = instance(),
            )
        }
    }
