package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private const val THRESHOLD = 1f

internal class DefaultFabNestedScrollConnection(private val scope: CoroutineScope = CoroutineScope(SupervisorJob())) :
    FabNestedScrollConnection {
    private val fabVisible = MutableStateFlow(true)

    override val isFabVisible: StateFlow<Boolean>
        get() =
            fabVisible
                .stateIn(
                    scope = scope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = true,
                )

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (available.y < -THRESHOLD) {
            fabVisible.value = false
        }
        if (available.y > THRESHOLD) {
            fabVisible.value = true
        }
        return Offset.Zero
    }
}
