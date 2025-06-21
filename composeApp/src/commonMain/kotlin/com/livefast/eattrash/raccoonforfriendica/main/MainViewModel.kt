package com.livefast.eattrash.raccoonforfriendica.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(private val inboxManager: InboxManager) :
    ViewModel(),
    MviModelDelegate<MainMviModel.Intent, MainMviModel.UiState, MainMviModel.Effect> by DefaultMviModelDelegate(
        initialState = MainMviModel.UiState(),
    ),
    MainMviModel {
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

    override fun reduce(intent: MainMviModel.Intent) {
        when (intent) {
            is MainMviModel.Intent.SetBottomBarOffsetHeightPx -> {
                viewModelScope.launch {
                    updateState { it.copy(bottomBarOffsetHeightPx = intent.value) }
                }
            }
        }
    }

    private fun getSections(inboxUnread: Int) = listOf(
        BottomNavigationSection.Home,
        BottomNavigationSection.Explore,
        BottomNavigationSection.Inbox(
            unreadItems = inboxUnread,
        ),
        BottomNavigationSection.Profile,
    )
}
