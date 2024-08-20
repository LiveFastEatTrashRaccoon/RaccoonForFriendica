package com.livefast.eattrash.raccoonforfriendica

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getAuthManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var loadingFinished = false
        installSplashScreen().setKeepOnScreenCondition {
            !loadingFinished
        }
        super.onCreate(savedInstanceState)

        val navigationCoordinator = getNavigationCoordinator()
        val drawerCoordinator = getDrawerCoordinator()

        val backPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // is the drawer is opened, close it
                    if (drawerCoordinator.drawerOpened.value) {
                        lifecycleScope.launch {
                            drawerCoordinator.closeDrawer()
                        }
                        return
                    }

                    // if in home, ask for confirmation
                    if (navigationCoordinator.currentSection.value == BottomNavigationSection.Home) {
                        // asks for confirmation
                        if (!navigationCoordinator.exitMessageVisible.value) {
                            navigationCoordinator.setExitMessageVisible(true)
                        }
                        return
                    }

                    // goes back to home
                    with(navigationCoordinator) {
                        setCurrentSection(BottomNavigationSection.Home)
                    }
                }
            }
        // when back is detected and the confirmation callback is not active, terminate the activity
        val finishBackPressedCallback =
            object : OnBackPressedCallback(false) {
                override fun handleOnBackPressed() {
                    navigationCoordinator.setExitMessageVisible(false)
                    finish()
                }
            }
        navigationCoordinator.exitMessageVisible
            .onEach { exitMessageVisible ->
                backPressedCallback.isEnabled = !exitMessageVisible
                finishBackPressedCallback.isEnabled = exitMessageVisible
            }.launchIn(lifecycleScope)
        onBackPressedDispatcher.addCallback(backPressedCallback)
        onBackPressedDispatcher.addCallback(finishBackPressedCallback)

        setContent {
            App(
                onLoadingFinished = {
                    loadingFinished = true
                },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.also {
            handleIncomingUrl(it)
        }
    }

    private fun handleIncomingUrl(uri: Uri) {
        if (uri.scheme == "raccoonforfriendica" && uri.host == "auth") {
            val authManager = getAuthManager()
            lifecycleScope.launch {
                kotlin.runCatching {
                    authManager.performTokenExchange(uri.toString())
                }
            }
        }
    }
}
