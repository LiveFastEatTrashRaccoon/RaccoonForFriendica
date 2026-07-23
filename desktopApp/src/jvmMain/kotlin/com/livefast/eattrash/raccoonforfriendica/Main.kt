@file:JvmName("Main")

package com.livefast.eattrash.raccoonforfriendica

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.livefast.eattrash.raccoonforfriendica.di.setupDi

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        e.printStackTrace()
    }

    setupDi()

    application {
        val windowState = rememberWindowState(size = DpSize(1024.dp, 768.dp))
        Window(
            onCloseRequest = {
                exitApplication()
            },
            title = "🦝 Raccoon",
            state = windowState,
        ) {
            App()
        }
    }
}
