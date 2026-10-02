package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list

import com.livefast.eattrash.raccoonforfriendica.core.architecture.Mvi

interface ShortcutListMvi : Mvi<ShortcutListMvi.Intent, ShortcutListMvi.State, ShortcutListMvi.Effect> {
    sealed interface Intent {
        data object Refresh : Intent

        data class Delete(val node: String) : Intent
    }

    data class State(
        val initial: Boolean = true,
        val refreshing: Boolean = false,
        val items: List<String> = emptyList(),
        val hideNavigationBarWhileScrolling: Boolean = true,
        val operationInProgress: Boolean = false,
    )

    sealed interface Effect {
        data object Failure : Effect
    }
}
