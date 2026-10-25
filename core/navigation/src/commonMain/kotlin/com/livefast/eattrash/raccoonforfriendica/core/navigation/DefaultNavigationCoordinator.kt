package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultNavigationCoordinator(
    private val navigationAdapterFactory: NavigationAdapterFactory,
    private val bottomNavigationAdapterFactory: BottomNavigationAdapterFactory,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : NavigationCoordinator {
    override val currentBottomNavSection = MutableStateFlow<BottomNavigationSection?>(null)
    override val onDoubleTabSelection = MutableSharedFlow<BottomNavigationSection>()
    override val canPop = MutableStateFlow(false)
    override val exitMessageVisible = MutableStateFlow(false)
    override val deepLinkUrl = MutableSharedFlow<String>()
    override val globalMessage = MutableSharedFlow<String>()

    private var rootNavAdapter: NavigationAdapter? = null
    private var bottomNavAdapter: BottomNavigationAdapter? = null
    private var sharedTransitionScope: SharedTransitionScope? = null
    private var bottomBarScrollConnection: NestedScrollConnection? = null
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var updateBottomNavigationSectionJob: Job? = null
    private var updateCanPopJob: Job? = null

    override fun setupRootNavigationAdapter(backStack: NavBackStack<NavKey>) {
        val adapter = navigationAdapterFactory.create(backStack)
        rootNavAdapter = adapter
        updateCanPopJob?.cancel()
        updateCanPopJob = adapter.canPop.onEach { newValue ->
            canPop.update { newValue }
        }.launchIn(scope)
    }

    override fun setupBottomNavigationAdapter(backStack: NavBackStack<NavKey>) {
        val adapter = bottomNavigationAdapterFactory.create(backStack)
        bottomNavAdapter = adapter
        updateBottomNavigationSectionJob?.cancel()
        updateBottomNavigationSectionJob = adapter.currentSection.onEach { newValue ->
            currentBottomNavSection.update { newValue }
        }.launchIn(scope)
    }

    override fun getSharedTransitionScope(): SharedTransitionScope =
        sharedTransitionScope ?: error("sharedTransitionScope not set")

    override fun setSharedTransitionScope(scope: SharedTransitionScope) {
        sharedTransitionScope = scope
    }

    override fun setCurrentSection(section: BottomNavigationSection) {
        val old = currentBottomNavSection.value
        if (old == section) {
            scope.launch {
                onDoubleTabSelection.emit(section)
            }
        } else {
            bottomNavAdapter?.navigate(section)
        }
    }

    override fun popUntilRoot() {
        rootNavAdapter?.popUntilRoot()
    }

    override fun setExitMessageVisible(value: Boolean) {
        exitMessageVisible.update { value }
    }

    override fun setBottomBarScrollConnection(connection: NestedScrollConnection?) {
        bottomBarScrollConnection = connection
    }

    override fun getBottomBarScrollConnection() = bottomBarScrollConnection

    override fun push(destination: Destination) {
        rootNavAdapter?.navigate(destination)
    }

    override fun replace(destination: Destination) {
        rootNavAdapter?.navigate(destination, replaceTop = true)
    }

    override fun pop() {
        rootNavAdapter?.pop()
    }

    override suspend fun submitDeeplink(url: String) {
        delay(750.milliseconds)
        deepLinkUrl.emit(url)
    }

    override fun showGlobalMessage(message: String, delay: Duration) {
        scope.launch {
            delay(delay)
            globalMessage.emit(message)
        }
    }
}
