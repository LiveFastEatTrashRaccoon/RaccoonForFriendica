package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation.NavController

interface BottomNavigationAdapter {
    fun navigate(section: BottomNavigationSection)
}

class DefaultBottomNavigationAdapter(private val navController: NavController) : BottomNavigationAdapter {
    override fun navigate(section: BottomNavigationSection) {
        navController.navigate(section)
    }
}
