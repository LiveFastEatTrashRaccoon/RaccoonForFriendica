package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DefaultBottomNavigationAdapter(private val backStack: NavBackStack<NavKey>) : BottomNavigationAdapter {

    override val currentSection = MutableStateFlow(backStack.lastOrNull() as? BottomNavigationSection)

    override fun navigate(section: BottomNavigationSection) {
        backStack[0] = section
        currentSection.update { old ->
            when (section) {
                is BottomNavigationSection.Inbox -> {
                    if (old is BottomNavigationSection.Inbox && section.unreadItems == 0) {
                        old
                    } else {
                        section
                    }
                }
                BottomNavigationSection.Profile -> BottomNavigationSection.Profile
                else -> section
            }
        }
    }
}
