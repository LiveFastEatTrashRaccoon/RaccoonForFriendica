package com.livefast.eattrash.raccoonforfriendica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var loadingFinished = false
        installSplashScreen().setKeepOnScreenCondition {
            !loadingFinished
        }
        super.onCreate(savedInstanceState)

        setContent {
            App(
                onLoadingFinished = {
                    loadingFinished = true
                },
            )
        }
    }
}
