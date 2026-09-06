package com.livefast.eattrash.raccoonforfriendica.core.navigation

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultDrawerCoordinator : DrawerCoordinator {
    override val events = MutableSharedFlow<DrawerEvent>()
    override val gesturesEnabled = MutableStateFlow(true)
    override val drawerOpened = MutableStateFlow(false)

    override suspend fun toggleDrawer() {
        events.emit(DrawerEvent.Toggle)
    }

    override suspend fun closeDrawer() {
        events.emit(DrawerEvent.Close)
    }

    override suspend fun sendEvent(event: DrawerEvent) {
        events.emit(event)
    }

    override fun setGesturesEnabled(value: Boolean) {
        gesturesEnabled.value = value
    }

    override fun changeDrawerOpened(value: Boolean) {
        drawerOpened.value = value
    }
}
