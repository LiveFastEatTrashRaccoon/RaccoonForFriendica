package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.l10n.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.L10nModule
import com.livefast.eattrash.raccoonforfriendica.core.resources.ProvideResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.ResourcesModule
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.mp.KoinPlatformTools
import org.koin.plugin.module.dsl.modules

@Composable
private fun SetupPreview(modules: List<Module> = listOf(), content: @Composable () -> Unit) {
    remember {
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            // first time: start Koin
            startKoin {
                modules(L10nModule::class, ResourcesModule::class)
                modules(modules)
            }
        } else {
            // already started: just load the extra modules
            loadKoinModules(modules)
        }
    }
    content()
}

@Composable
fun PreviewWrapper(modules: List<Module> = listOf(), content: @Composable () -> Unit) {
    SetupPreview(modules) {
        ProvideResources {
            ProvideStrings("en") {
                content()
            }
        }
    }
}
