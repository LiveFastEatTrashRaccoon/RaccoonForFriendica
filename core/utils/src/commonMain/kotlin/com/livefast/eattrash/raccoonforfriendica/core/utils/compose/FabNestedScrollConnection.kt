package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import kotlinx.coroutines.flow.StateFlow

interface FabNestedScrollConnection : NestedScrollConnection {
    val isFabVisible: StateFlow<Boolean>
}
