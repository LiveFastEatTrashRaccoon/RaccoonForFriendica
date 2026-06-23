package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class DefaultNavigationAdapter(
    private val backStack: NavBackStack<NavKey>,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : NavigationAdapter {

    override val canPop = MutableStateFlow(false)
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var job: Job? = null

    init {
        updateCanPop()
    }

    override fun navigate(destination: Destination, replaceTop: Boolean) {
        if (job?.isActive == true) {
            return
        }
        perform {
            if (replaceTop) {
                backStack[backStack.lastIndex] = destination
            } else {
                backStack.add(destination)
            }
            updateCanPop()
        }
    }

    override fun pop() {
        if (job?.isActive == true) {
            return
        }
        perform {
            if (canPop.value) {
                backStack.removeLast()
            }
            updateCanPop()
        }
    }

    override fun popUntilRoot() {
        if (job?.isActive == true) {
            return
        }
        perform {
            backStack.retainAll { it == backStack.first() }
            updateCanPop()
        }
    }

    private fun updateCanPop() {
        canPop.update { backStack.size > 1 }
    }

    private fun perform(interval: Duration = 250.milliseconds, action: () -> Unit) {
        job = scope.launch {
            action()
            delay(interval)
            job = null
        }
    }
}
