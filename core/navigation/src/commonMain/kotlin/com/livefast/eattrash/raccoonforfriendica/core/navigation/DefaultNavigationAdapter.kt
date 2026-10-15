package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@AssistedInject
class DefaultNavigationAdapter(@Assisted private val backStack: NavBackStack<NavKey>) : NavigationAdapter {

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

@ContributesBinding(AppScope::class)
@AssistedFactory
interface DefaultNavigationAdapterFactory : NavigationAdapterFactory {
    override fun create(@Assisted backStack: NavBackStack<NavKey>): DefaultNavigationAdapter
}
