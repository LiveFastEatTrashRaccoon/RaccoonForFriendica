package com.github.akesiseli.raccoonforfriendica.bottomnavigation

import cafe.adriel.voyager.navigator.tab.Tab
import com.github.akesiseli.raccoonforfriendica.core.navigation.BottomNavigationSection

fun BottomNavigationSection.toTab(): Tab =
    when (this) {
        BottomNavigationSection.Explore -> ExploreTab
        BottomNavigationSection.Inbox -> InboxTab
        BottomNavigationSection.Profile -> ProfileTab
        else -> HomeTab
    }
