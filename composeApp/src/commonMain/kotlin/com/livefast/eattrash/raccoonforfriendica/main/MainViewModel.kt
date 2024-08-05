package com.livefast.eattrash.raccoonforfriendica.main

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import kotlinx.coroutines.launch

class MainViewModel :
    DefaultMviModel<MainMviModel.Intent, MainMviModel.UiState, MainMviModel.Effect>(
        initialState =
            MainMviModel.UiState(
                bottomNavigationSections =
                    listOf(
                        BottomNavigationSection.Home,
                        BottomNavigationSection.Explore,
                        BottomNavigationSection.Inbox,
                        BottomNavigationSection.Profile,
                    ),
            ),
    ),
    MainMviModel {
    override fun reduce(intent: MainMviModel.Intent) {
        when (intent) {
            is MainMviModel.Intent.SetBottomBarOffsetHeightPx -> {
                screenModelScope.launch {
                    updateState { it.copy(bottomBarOffsetHeightPx = intent.value) }
                }
            }
        }
    }
}
