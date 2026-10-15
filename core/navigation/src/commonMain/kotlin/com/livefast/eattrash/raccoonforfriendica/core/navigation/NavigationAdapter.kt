package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.StateFlow

interface NavigationAdapterFactory {
    fun create(backStack: NavBackStack<NavKey>): NavigationAdapter
}

interface NavigationAdapter {
    val canPop: StateFlow<Boolean>

    fun navigate(destination: Destination, replaceTop: Boolean = false)

    fun pop()

    fun popUntilRoot()
}
