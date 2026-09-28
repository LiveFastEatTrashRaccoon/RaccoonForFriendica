package com.livefast.eattrash.raccoonforfriendica.core.navigation

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface DrawerEvent {
    data object Toggle : DrawerEvent

    data object Close : DrawerEvent
}

interface DrawerCoordinator {
    val events: SharedFlow<DrawerEvent>
    val gesturesEnabled: StateFlow<Boolean>
    val drawerOpened: StateFlow<Boolean>

    suspend fun toggleDrawer()

    suspend fun closeDrawer()

    suspend fun sendEvent(event: DrawerEvent)

    fun setGesturesEnabled(value: Boolean)

    fun changeDrawerOpened(value: Boolean)
}
