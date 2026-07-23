package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.di

import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val nodeInfoModule = module {
    viewModel {
        NodeInfoViewModel(
            apiConfigurationRepository = get(),
            identityRepository = get(),
            nodeInfoRepository = get(),
            credentialsRepository = get(),
            supportedFeatureRepository = get(),
            settingsRepository = get(),
            emojiHelper = get(),
            imageAutoloadObserver = get(),
        )
    }
}
