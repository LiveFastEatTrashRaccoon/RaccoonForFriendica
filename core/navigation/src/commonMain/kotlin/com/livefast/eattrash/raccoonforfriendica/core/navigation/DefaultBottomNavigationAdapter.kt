package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class DefaultBottomNavigationAdapter(
    private val navController: NavController,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : BottomNavigationAdapter {

    override val currentSection = MutableStateFlow<BottomNavigationSection?>(null)
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        navController.currentBackStackEntryFlow.onEach { entry ->
            val destination = entry.destination
            currentSection.update { old ->
                when {
                    destination.hasRoute<BottomNavigationSection.Home>() -> BottomNavigationSection.Home
                    destination.hasRoute<BottomNavigationSection.Explore>() -> BottomNavigationSection.Explore
                    destination.hasRoute<BottomNavigationSection.Inbox>() -> {
                        val newInbox = entry.toRoute<BottomNavigationSection.Inbox>()
                        if (old is BottomNavigationSection.Inbox && newInbox.unreadItems == 0) {
                            old
                        } else {
                            newInbox
                        }
                    }
                    destination.hasRoute<BottomNavigationSection.Profile>() -> BottomNavigationSection.Profile
                    else -> BottomNavigationSection.Home
                }
            }
        }.launchIn(scope)
    }

    override fun navigate(section: BottomNavigationSection) {
        navController.navigate(section)
    }
}
