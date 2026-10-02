package com.livefast.eattrash.raccoonforfriendica.main

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection

interface MainMvi : Mvi<MainMvi.Intent, MainMvi.UiState, MainMvi.Effect> {
    sealed interface Intent {
        data class SetBottomBarOffsetHeightPx(val px: Float) : Intent
    }

    data class UiState(
        val bottomBarOffsetHeightPx: Float = 0f,
        val bottomNavigationSections: List<BottomNavigationSection> = emptyList(),
    )

    sealed interface Effect
}
