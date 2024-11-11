package com.livefast.eattrash.raccoonforfriendica.core.navigation

import cafe.adriel.voyager.core.screen.Screen

interface NavigatorAdapter {
    val canPop: Boolean

    fun push(screen: Screen)

    fun pop()

    fun popUntilRoot()

    fun replace(screen: Screen)
}
