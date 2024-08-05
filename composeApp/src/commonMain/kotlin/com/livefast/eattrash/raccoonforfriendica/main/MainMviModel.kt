package com.livefast.eattrash.raccoonforfriendica.main

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection

interface MainMviModel :
    MviModel<MainMviModel.Intent, MainMviModel.UiState, MainMviModel.Effect>,
    ScreenModel {
    sealed interface Intent {
        data class SetBottomBarOffsetHeightPx(
            val value: Float,
        ) : Intent
    }

    data class UiState(
        val bottomBarOffsetHeightPx: Float = 0f,
        val isLogged: Boolean = false,
        val bottomNavigationSections: List<BottomNavigationSection> = emptyList(),
    )

    sealed interface Effect
}
