package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.DefaultFabNestedScrollConnection
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import org.koin.dsl.module

val coreCommonUiComponentsModule =
    module {
        factory<FabNestedScrollConnection> {
            DefaultFabNestedScrollConnection()
        }
    }
