package com.livefast.eattrash.raccoonforfriendica.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val inboxManager: InboxManager,
    private val identityRepository: IdentityRepository,
) : DefaultMviModel<MainMviModel.Intent, MainMviModel.UiState, MainMviModel.Effect>(
        initialState = MainMviModel.UiState(),
    ),
    MainMviModel {
    init {
        screenModelScope.launch {
            inboxManager.unreadCount
                .onEach { inboxUnread ->
                    updateState {
                        it.copy(bottomNavigationSections = getSections(inboxUnread))
                    }
                }.launchIn(this)
            identityRepository.isLogged
                .onEach {
                    inboxManager.refreshUnreadCount()
                }.launchIn(this)
        }
    }

    override fun reduce(intent: MainMviModel.Intent) {
        when (intent) {
            is MainMviModel.Intent.SetBottomBarOffsetHeightPx -> {
                screenModelScope.launch {
                    updateState { it.copy(bottomBarOffsetHeightPx = intent.value) }
                }
            }
        }
    }

    private fun getSections(inboxUnread: Int) =
        listOf(
            BottomNavigationSection.Home,
            BottomNavigationSection.Explore,
            BottomNavigationSection.Inbox(
                unreadItems = inboxUnread,
            ),
            BottomNavigationSection.Profile,
        )
}
