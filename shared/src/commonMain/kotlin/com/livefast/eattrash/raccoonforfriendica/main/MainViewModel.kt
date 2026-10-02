package com.livefast.eattrash.raccoonforfriendica.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class MainViewModel(private val inboxManager: InboxManager) :
    ViewModel(),
    MviDelegate<MainMvi.Intent, MainMvi.UiState, MainMvi.Effect> by DefaultMviDelegate(
        initialState = MainMvi.UiState(),
    ),
    MainMvi {
    init {
        viewModelScope.launch {
            inboxManager.unreadCount
                .onEach { inboxUnread ->
                    updateState {
                        it.copy(bottomNavigationSections = getSections(inboxUnread))
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: MainMvi.Intent) {
        when (intent) {
            is MainMvi.Intent.SetBottomBarOffsetHeightPx -> {
                viewModelScope.launch {
                    updateState { it.copy(bottomBarOffsetHeightPx = intent.value) }
                }
            }
        }
    }

    private fun getSections(inboxUnread: Int) = listOf(
        BottomNavigationSection.Home,
        BottomNavigationSection.Explore,
        BottomNavigationSection.Inbox(unreadItems = inboxUnread),
        BottomNavigationSection.Profile,
    )
}
