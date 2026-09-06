package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private const val THRESHOLD = 1f

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultFabNestedScrollConnection : FabNestedScrollConnection {

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
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
