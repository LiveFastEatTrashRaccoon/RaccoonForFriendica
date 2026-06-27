package com.livefast.eattrash.raccoonforfriendica

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.livefast.eattrash.raccoonforfriendica.auth.DefaultAuthManager
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.di.getAuthManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var loadingFinished = false
        installSplashScreen().setKeepOnScreenCondition {
            !loadingFinished
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val navigationCoordinator = getNavigationCoordinator()
        val backPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navigationCoordinator.canPop.value) {
                        navigationCoordinator.pop()
                        return
                    }

                    // if in home, ask for confirmation
                    if (navigationCoordinator.currentBottomNavSection.value == BottomNavigationSection.Home) {
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
        with(onBackPressedDispatcher) {
            addCallback(backPressedCallback)
            addCallback(finishBackPressedCallback)
        }

        setContent {
            App(
                onLoadingFinished = {
                    loadingFinished = true
                },
            )
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when {
            intent.action == Intent.ACTION_SEND -> handleIncomingAttachment(intent)
            intent.getBooleanExtra(InboxManager.OPEN_INBOX_AT_STARTUP, false) -> handleOpenInboxAtStartup()
            else -> intent.data?.also { handleIncomingUrl(it) }
        }
    }

    private fun handleIncomingUrl(uri: Uri) {
        when {
            // OAuth2 redirect URL
            uri.scheme == DefaultAuthManager.REDIRECT_SCHEME &&
                uri.host == DefaultAuthManager.REDIRECT_HOST -> {
                val authManager = getAuthManager()
                lifecycleScope.launch {
                    try {
                        authManager.performTokenExchange(uri.toString())
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                    }
                }
            }

            else -> {
                // try opening deep link URL
                uri.toString().takeUnless { it.isEmpty() }?.also { deeplinkUrl ->
                    lifecycleScope.launch {
                        val navigationCoordinator = getNavigationCoordinator()
                        navigationCoordinator.submitDeeplink(deeplinkUrl)
                    }
                }
            }
        }
    }

    private fun handleIncomingAttachment(intent: Intent) {
        lifecycleScope.launch {
            // workaround: wait until the root NavigationAdapter has been set
            delay(750.milliseconds)
            val mainRouter = getMainRouter()
            when {
                "text/plain" == intent.type -> {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.also { content ->
                        mainRouter.openComposer(initialText = content.trim('"'))
                    }
                }

                intent.type?.startsWith("image/") == true -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                    }?.also { uri ->
                        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        if (bytes != null) {
                            mainRouter.openComposer(initialAttachment = bytes)
                        }
                    }
                }
            }
        }
    }

    private fun handleOpenInboxAtStartup() {
        lifecycleScope.launch {
            // workaround: wait until the root NavigationAdapter has been set
            delay(1.seconds)
            val navigationCoordinator = getNavigationCoordinator()
            navigationCoordinator.popUntilRoot()
            navigationCoordinator.setCurrentSection(BottomNavigationSection.Inbox())
        }
    }
}
