package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DefaultNavigationAdapter(private val backStack: NavBackStack<NavKey>) : NavigationAdapter {

    override val canPop = MutableStateFlow(false)

    init {
        updateCanPop()
    }

    override fun navigate(destination: Destination, replaceTop: Boolean) {
        if (replaceTop) {
            backStack[backStack.lastIndex] = destination
        } else {
            backStack.add(destination)
        }
        updateCanPop()
    }

    override fun pop() {
        if (canPop.value) {
            backStack.removeLast()
        }
        updateCanPop()
    }

    override fun popUntilRoot() {
        backStack.retainAll { it == backStack.first() }
        updateCanPop()
    }

    private fun updateCanPop() {
        canPop.update { backStack.size > 1 }
    }
}
