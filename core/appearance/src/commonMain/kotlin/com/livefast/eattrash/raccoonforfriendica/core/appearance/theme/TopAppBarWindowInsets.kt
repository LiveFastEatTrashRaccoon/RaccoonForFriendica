package com.livefast.eattrash.raccoonforfriendica.core.appearance.theme

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberMaxTopBarInset(): Dp {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density).let { with(density) { it.toDp() } }
    return maxOf(statusBarHeight, Dimensions.maxTopBarInset)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarState.toWindowInsets(): WindowInsets {
    val maxInset = rememberMaxTopBarInset()

    var topInset by remember { mutableStateOf(maxInset) }

    LaunchedEffect(maxInset, this) {
        snapshotFlow {
            maxInset * (1 - collapsedFraction)
        }.collect {
            topInset = it
        }
    }
    return WindowInsets(0.dp, topInset, 0.dp, 0.dp)
}
