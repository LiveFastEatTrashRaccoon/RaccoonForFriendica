package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation.NavController

interface NavigationAdapter {
    val canPop: Boolean

    fun navigate(destination: Destination, replaceTop: Boolean = false)

    fun pop()

    fun popUntilRoot()
}

class DefaultNavigationAdapter(private val navController: NavController) : NavigationAdapter {
    override var canPop: Boolean = false

    override fun navigate(destination: Destination, replaceTop: Boolean) {
        if (replaceTop) {
            navController.popBackStack()
        }
        navController.navigate(destination)
        canPop = navController.currentBackStack.value.size > 1
    }

    override fun pop() {
        navController.popBackStack()
        canPop = navController.currentBackStack.value.size > 1
    }

    override fun popUntilRoot() {
        do {
            pop()
        } while (canPop)
    }
}
