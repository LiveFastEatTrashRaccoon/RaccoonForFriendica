package com.livefast.eattrash.raccoonforfriendica.core.navigation

import kotlinx.coroutines.flow.StateFlow

interface BottomNavigationAdapter {
    val currentSection: StateFlow<BottomNavigationSection?>
    fun navigate(section: BottomNavigationSection)
}
