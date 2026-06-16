package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.l10nModule
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.resourcesModule
import org.kodein.di.DI
import org.kodein.di.instance

fun getFabNestedScrollConnection(): FabNestedScrollConnection {
    val res by RootDI.di.instance<FabNestedScrollConnection>()
    return res
}

@Composable
fun rememberFabNestedScrollConnection() = remember { getFabNestedScrollConnection() }

fun RootDI.setupPreview() {
    di = DI {
        importAll(resourcesModule, l10nModule)
    }
}
