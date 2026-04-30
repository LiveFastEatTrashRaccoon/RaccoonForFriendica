package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.DefaultFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import org.kodein.di.DI
import org.kodein.di.bindSingleton

val commonUiComponentsModule =
    DI.Module("CommonUiComponentsModule") {
        bindSingleton<FabNestedScrollConnection> {
            DefaultFabNestedScrollConnection()
        }
    }
