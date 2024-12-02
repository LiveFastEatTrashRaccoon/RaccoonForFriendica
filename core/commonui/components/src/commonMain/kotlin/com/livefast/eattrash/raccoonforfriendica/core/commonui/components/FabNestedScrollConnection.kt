package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import kotlinx.coroutines.flow.StateFlow

@Stable
interface FabNestedScrollConnection : NestedScrollConnection {
    val isFabVisible: StateFlow<Boolean>
}
