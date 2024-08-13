package com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di

import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.FabNestedScrollConnection
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getFabNestedScrollConnection(): FabNestedScrollConnection = LemmyUiDiHelper.fabNestedScrollConnection

object LemmyUiDiHelper : KoinComponent {
    val fabNestedScrollConnection: FabNestedScrollConnection by inject()
}
