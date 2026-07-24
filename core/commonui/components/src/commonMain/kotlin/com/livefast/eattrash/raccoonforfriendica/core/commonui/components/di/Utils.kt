package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.di.getByInjection
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.l10nModule
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.resourcesModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun getFabNestedScrollConnection(): FabNestedScrollConnection = getByInjection(FabNestedScrollConnection::class)

@Composable
fun rememberFabNestedScrollConnection() = remember { getFabNestedScrollConnection() }

@Composable
fun setupPreview(vararg modules: Module): KoinApplication = remember {
    startKoin {
        modules(resourcesModule, l10nModule, *modules)
    }
}
