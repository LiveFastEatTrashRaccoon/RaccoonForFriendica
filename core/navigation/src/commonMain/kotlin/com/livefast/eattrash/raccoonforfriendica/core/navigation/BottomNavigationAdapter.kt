package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.StateFlow

interface BottomNavigationAdapter {
    val currentSection: StateFlow<BottomNavigationSection?>
    fun navigate(section: BottomNavigationSection)
}

fun interface BottomNavigationAdapterFactory {
    fun create(backStack: NavBackStack<NavKey>): BottomNavigationAdapter
}
