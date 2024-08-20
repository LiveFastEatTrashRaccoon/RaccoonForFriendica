package com.livefast.eattrash.raccoonforfriendica.bottomnavigation

import cafe.adriel.voyager.navigator.tab.Tab
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection

fun BottomNavigationSection.toTab(): Tab =
    when (this) {
        BottomNavigationSection.Explore -> ExploreTab
        is BottomNavigationSection.Inbox -> InboxTab
        BottomNavigationSection.Profile -> ProfileTab
        else -> HomeTab
    }
