package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getFabNestedScrollConnection(): FabNestedScrollConnection = UiComponentDiHelper.fabNestedScrollConnection

object UiComponentDiHelper : KoinComponent {
    val fabNestedScrollConnection: FabNestedScrollConnection by inject()
}
