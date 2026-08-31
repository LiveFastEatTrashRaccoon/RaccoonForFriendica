package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.DummyUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.ProvideUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.UiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.ProvideStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.L10nModule
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.ProvideResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.ResourcesModule
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import org.koin.plugin.module.dsl.modules

@Composable
private fun SetupPreview(modules: List<Module> = listOf(), content: @Composable () -> Unit) {
    val uiDepsModule = remember {
        module {
            single<UiDeps> {
                object : DummyUiDeps() {
                    override val resources: CoreResources = get()
                    override val strings: Strings = get()
                }
            }
        }
    }
    remember {
        if (KoinPlatformTools.defaultContext().getOrNull() == null) {
            // first time: start Koin
            startKoin {
                allowOverride(true)
                modules(L10nModule::class, ResourcesModule::class)
                modules(uiDepsModule + modules)
            }
        } else {
            // already started: just load the extra modules
            loadKoinModules(modules + uiDepsModule)
        }
    }
    content()
}

@Composable
fun PreviewWrapper(modules: List<Module> = listOf(), content: @Composable () -> Unit) {
    SetupPreview(modules) {
        val uiDeps: UiDeps = koinInject()
        ProvideUiDeps(uiDeps) {
            ProvideResources(resources = uiDeps.resources) {
                ProvideStrings(lang = "en", strings = uiDeps.strings) {
                    content()
                }
            }
        }
    }
}
