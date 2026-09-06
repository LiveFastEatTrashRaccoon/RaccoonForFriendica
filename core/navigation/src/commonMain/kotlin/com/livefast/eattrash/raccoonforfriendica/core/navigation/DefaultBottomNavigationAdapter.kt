package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
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
