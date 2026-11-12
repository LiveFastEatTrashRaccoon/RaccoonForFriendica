package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@ContributesBinding(AppScope::class)
@Inject
class DefaultFabNestedScrollConnection : FabNestedScrollConnection {
    private val fabVisible = MutableStateFlow(true)
    private var accumulatedDelta = 0f

    override val isFabVisible = fabVisible.asStateFlow()

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        if (delta < 0f) {
            if (accumulatedDelta > 0f) {
                accumulatedDelta = 0f
            }
            accumulatedDelta += delta
            if (accumulatedDelta < HIDE_THRESHOLD) {
                fabVisible.value = false
            }
        } else if (delta > 0f) {
            if (accumulatedDelta < 0f) {
                accumulatedDelta = 0f
            }
            accumulatedDelta += delta
            if (accumulatedDelta > SHOW_THRESHOLD) {
                fabVisible.value = true
            }
        }
        return Offset.Zero
    }

    companion object {
        private const val HIDE_THRESHOLD = -20f
        private const val SHOW_THRESHOLD = 50f
    }
}
