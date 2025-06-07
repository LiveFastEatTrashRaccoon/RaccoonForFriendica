package com.livefast.eattrash.raccoonforfriendica.core.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

data class VoyagerNavigator(val adaptee: Navigator) : NavigatorAdapter {
    override val canPop: Boolean
        get() = adaptee.canPop

    override fun push(screen: Screen) {
        adaptee.push(screen)
    }

    override fun pop() {
        adaptee.pop()
    }

    override fun popUntilRoot() {
        adaptee.popUntilRoot()
    }

    override fun replace(screen: Screen) {
        adaptee.replace(screen)
    }
}
