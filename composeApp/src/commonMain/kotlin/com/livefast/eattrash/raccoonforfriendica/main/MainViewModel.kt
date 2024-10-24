package com.livefast.eattrash.raccoonforfriendica.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val inboxManager: InboxManager,
    private val settingsRepository: SettingsRepository,
    private val pullNotificationManager: PullNotificationManager,
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
            settingsRepository.current
                .map {
                    it?.pullNotificationCheckInterval
                }.distinctUntilChanged()
                .onEach {
                    val minutes = it?.inWholeMinutes
                    if (minutes != null) {
                        pullNotificationManager.setPeriod(minutes)
                        pullNotificationManager.start()
                    } else {
                        pullNotificationManager.stop()
                    }
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
