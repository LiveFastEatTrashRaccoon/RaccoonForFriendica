@file:JvmName("Main")
package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.logDebug
import com.livefast.eattrash.raccoonforfriendica.di.initDi

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        e.printStackTrace()
    }

    initDi()

    application {
        logDebug("App starting…")

        val windowState = rememberWindowState()

        Window(
            onCloseRequest = {
                exitApplication()
            },
            title = "Raccoon 🦝",
            state = windowState,
        ) {
            App()
        }
    }
}
